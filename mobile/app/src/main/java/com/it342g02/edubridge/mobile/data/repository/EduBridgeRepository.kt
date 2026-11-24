package com.it342g02.edubridge.mobile.data.repository

import com.it342g02.edubridge.mobile.data.model.Announcement
import com.it342g02.edubridge.mobile.data.model.Attendance
import com.it342g02.edubridge.mobile.data.model.AttendanceStatus
import com.it342g02.edubridge.mobile.data.model.DeliveryMethod
import com.it342g02.edubridge.mobile.data.model.FamilyLearningMission
import com.it342g02.edubridge.mobile.data.model.Grade
import com.it342g02.edubridge.mobile.data.model.Homework
import com.it342g02.edubridge.mobile.data.model.HomeworkSubmission
import com.it342g02.edubridge.mobile.data.model.LearningMaterial
import com.it342g02.edubridge.mobile.data.model.Lesson
import com.it342g02.edubridge.mobile.data.model.LessonProgress
import com.it342g02.edubridge.mobile.data.model.MissionParticipation
import com.it342g02.edubridge.mobile.data.model.Notification
import com.it342g02.edubridge.mobile.data.model.ParentStudent
import com.it342g02.edubridge.mobile.data.model.PointTransaction
import com.it342g02.edubridge.mobile.data.model.Role
import com.it342g02.edubridge.mobile.data.model.SchoolClass
import com.it342g02.edubridge.mobile.data.model.Status
import com.it342g02.edubridge.mobile.data.model.StudentClass
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.data.model.UserBadge
import com.it342g02.edubridge.mobile.data.remote.EduBridgeApi
import com.it342g02.edubridge.mobile.util.Dates
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// ---------- Aggregates the screens render ----------

data class TeacherDashboard(
    val classes: List<SchoolClass>,
    val studentCount: Int,
    val homework: List<Homework>,
    val missions: List<FamilyLearningMission>,
    val announcements: List<Announcement>,
)

data class ClassDetail(
    val schoolClass: SchoolClass,
    val enrollments: List<StudentClass>,
    val lessons: List<Lesson>,
    val homework: List<Homework>,
    val missions: List<FamilyLearningMission>,
    val announcements: List<Announcement>,
)

/** Everything about one enrollment (a student in one class). */
data class EnrollmentRecord(
    val enrollment: StudentClass,
    val grades: List<Grade>,
    val attendance: List<Attendance>,
)

/**
 * A student's full picture across all their classes. Used by the student app and by
 * parents viewing a linked child, so both roles see the same numbers.
 */
data class StudentOverview(
    val student: User,
    val records: List<EnrollmentRecord>,
    val lessons: List<Lesson>,
    val homework: List<Homework>,
    val missions: List<FamilyLearningMission>,
    val announcements: List<Announcement>,
    val submissions: List<HomeworkSubmission>,
    val lessonProgress: List<LessonProgress>,
    val points: List<PointTransaction>,
    val badges: List<UserBadge>,
) {
    val classes: List<SchoolClass> get() = records.mapNotNull { it.enrollment.schoolClass }
    val grades: List<Grade> get() = records.flatMap { it.grades }
    val attendance: List<Attendance> get() = records.flatMap { it.attendance }
    val totalPoints: Int get() = points.sumOf { it.points ?: 0 }

    fun submissionFor(homework: Homework): HomeworkSubmission? =
        submissions.firstOrNull { it.homework?.id == homework.id }

    fun isLessonDone(lesson: Lesson): Boolean = lessonProgress.any {
        it.lesson?.id == lesson.id && it.status.equals(Status.COMPLETED, ignoreCase = true)
    }

    /** Pending = not yet submitted. */
    val pendingHomework: List<Homework> get() = homework.filter { submissionFor(it) == null }

    /** Average of grade percentages, or null when nothing is graded yet. */
    val averageGrade: Double? get() = grades.mapNotNull { it.percent }.takeIf { it.isNotEmpty() }?.average()

    /** Present + late + excused count as attended. */
    val attendanceRate: Double?
        get() = attendance.takeIf { it.isNotEmpty() }?.let { list ->
            list.count { it.attendanceStatus != AttendanceStatus.ABSENT } * 100.0 / list.size
        }
}

data class HomeworkReview(
    val homework: Homework,
    val submissions: List<HomeworkSubmission>,
    val roster: List<StudentClass>,
) {
    val missing: List<StudentClass>
        get() = roster.filter { e -> submissions.none { it.student?.id == e.student?.id } }
}

data class MissionReview(
    val mission: FamilyLearningMission,
    val participations: List<MissionParticipation>,
    val roster: List<StudentClass>,
)

data class AttendanceRow(val enrollment: StudentClass, val existing: Attendance?)

data class MissionWithProgress(
    val mission: FamilyLearningMission,
    val participation: MissionParticipation?,
)

/**
 * All API orchestration lives here so screens stay declarative. Lists are fetched in
 * parallel per class because the backend filters by one id at a time.
 */
class EduBridgeRepository(private val api: EduBridgeApi) {

    // ======================= Shared =======================

    suspend fun notifications(userId: Long): List<Notification> =
        api.notifications(userId).sortedByDescending { it.sentAt ?: "" }

    suspend fun markRead(notification: Notification): Notification =
        api.updateNotification(notification.id!!, notification.copy(status = Status.READ))

    suspend fun markAllRead(userId: Long) = coroutineScope {
        api.notifications(userId).filter { it.isUnread }
            .map { async { markRead(it) } }.awaitAll()
    }

    suspend fun updateProfile(user: User): User = api.updateUser(user.id!!, user)

    suspend fun materials(lessonId: Long): List<LearningMaterial> = api.learningMaterials(lessonId)

    // ======================= Teacher =======================

    suspend fun teacherDashboard(teacherId: Long): TeacherDashboard = coroutineScope {
        val classes = api.classes(teacherId)
        val perClass = classes.map { c ->
            async {
                val id = c.id!!
                val enrollments = async { api.studentClasses(classId = id) }
                val homework = async { api.homework(id) }
                val missions = async { api.familyMissions(id) }
                val announcements = async { api.announcements(id) }
                ClassBundle(enrollments.await(), homework.await(), missions.await(), announcements.await())
            }
        }.awaitAll()

        TeacherDashboard(
            classes = classes,
            studentCount = perClass.flatMap { b -> b.enrollments.mapNotNull { it.student?.id } }.toSet().size,
            homework = perClass.flatMap { it.homework }.sortedBy { it.dueDate ?: "9999" },
            missions = perClass.flatMap { it.missions },
            announcements = perClass.flatMap { it.announcements }.sortedByDescending { it.createdAt ?: "" },
        )
    }

    private data class ClassBundle(
        val enrollments: List<StudentClass>,
        val homework: List<Homework>,
        val missions: List<FamilyLearningMission>,
        val announcements: List<Announcement>,
    )

    suspend fun teacherClasses(teacherId: Long): List<SchoolClass> = api.classes(teacherId)

    suspend fun createClass(teacher: User, name: String, gradeLevel: String, schoolYear: String) =
        api.createClass(
            SchoolClass(
                teacher = User(id = teacher.id),
                className = name,
                gradeLevel = gradeLevel.ifBlank { null },
                schoolYear = schoolYear.ifBlank { null },
                createdAt = Dates.nowIso(),
            ),
        )

    suspend fun classDetail(classId: Long): ClassDetail = coroutineScope {
        val schoolClass = async { api.schoolClass(classId) }
        val enrollments = async { api.studentClasses(classId = classId) }
        val lessons = async { api.lessons(classId) }
        val homework = async { api.homework(classId) }
        val missions = async { api.familyMissions(classId) }
        val announcements = async { api.announcements(classId) }
        ClassDetail(
            schoolClass = schoolClass.await(),
            enrollments = enrollments.await().sortedBy { it.student?.lastName ?: "" },
            lessons = lessons.await().sortedByDescending { it.publishedAt ?: "" },
            homework = homework.await().sortedByDescending { it.dueDate ?: "" },
            missions = missions.await().sortedByDescending { it.createdAt ?: "" },
            announcements = announcements.await().sortedByDescending { it.createdAt ?: "" },
        )
    }

    suspend fun allStudents(): List<User> = api.users(Role.STUDENT).sortedBy { it.lastName ?: "" }

    suspend fun enroll(classId: Long, studentId: Long) = api.createStudentClass(
        StudentClass(
            student = User(id = studentId),
            schoolClass = SchoolClass(id = classId),
            enrollmentDate = Dates.todayIso(),
        ),
    )

    suspend fun unenroll(enrollmentId: Long) {
        api.deleteStudentClass(enrollmentId)
    }

    suspend fun createLesson(classId: Long, title: String, description: String, objective: String, content: String) =
        api.createLesson(
            Lesson(
                schoolClass = SchoolClass(id = classId),
                title = title,
                description = description.ifBlank { null },
                learningObjective = objective.ifBlank { null },
                content = content.ifBlank { null },
                publishedAt = Dates.nowIso(),
                status = Status.PUBLISHED,
            ),
        )

    suspend fun addMaterial(lessonId: Long, name: String, type: String, url: String) =
        api.createLearningMaterial(
            LearningMaterial(
                lesson = Lesson(id = lessonId),
                materialName = name,
                materialType = type.ifBlank { null },
                fileUrl = url.ifBlank { null },
                createdAt = Dates.nowIso(),
            ),
        )

    suspend fun createHomework(classId: Long, title: String, description: String, dueDate: String?): Homework {
        val created = api.createHomework(
            Homework(
                schoolClass = SchoolClass(id = classId),
                title = title,
                description = description.ifBlank { null },
                dueDate = dueDate,
                createdAt = Dates.nowIso(),
                status = Status.ASSIGNED,
            ),
        )
        notifyClass(classId, includeParents = true, type = "HOMEWORK", message = "New homework: $title") {
            it.copy(homework = Homework(id = created.id))
        }
        return created
    }

    suspend fun createAnnouncement(classId: Long, title: String, message: String, highPriority: Boolean): Announcement {
        val created = api.createAnnouncement(
            Announcement(
                schoolClass = SchoolClass(id = classId),
                title = title,
                message = message,
                createdAt = Dates.nowIso(),
                priority = if (highPriority) Status.HIGH else Status.NORMAL,
            ),
        )
        notifyClass(classId, includeParents = true, type = "ANNOUNCEMENT", message = title) {
            it.copy(announcement = Announcement(id = created.id))
        }
        return created
    }

    suspend fun createMission(
        classId: Long,
        title: String,
        description: String,
        objective: String,
        instructions: String,
        points: Int,
        dueDate: String?,
    ): FamilyLearningMission {
        val created = api.createFamilyMission(
            FamilyLearningMission(
                schoolClass = SchoolClass(id = classId),
                title = title,
                description = description.ifBlank { null },
                learningObjective = objective.ifBlank { null },
                instructions = instructions.ifBlank { null },
                dueDate = dueDate,
                points = points,
                createdAt = Dates.nowIso(),
                status = Status.ACTIVE,
            ),
        )
        notifyClass(classId, includeParents = true, type = "MISSION", message = "New family mission: $title") {
            it.copy(mission = FamilyLearningMission(id = created.id))
        }
        return created
    }

    /**
     * Sends an in-app notification to every student in the class (and their linked
     * parents). Best effort: a failure here never undoes the item that was created.
     */
    private suspend fun notifyClass(
        classId: Long,
        includeParents: Boolean,
        type: String,
        message: String,
        attach: (Notification) -> Notification,
    ) {
        try {
            coroutineScope {
                val studentIds = api.studentClasses(classId = classId).mapNotNull { it.student?.id }.toSet()
                val parentIds = if (includeParents) {
                    studentIds.map { id -> async { api.parentStudents(studentId = id) } }.awaitAll()
                        .flatten().mapNotNull { it.parent?.id }.toSet()
                } else {
                    emptySet()
                }
                (studentIds + parentIds).map { userId ->
                    async {
                        api.createNotification(
                            attach(
                                Notification(
                                    user = User(id = userId),
                                    notificationType = type,
                                    message = message,
                                    deliveryMethod = DeliveryMethod.PUSH,
                                    sentAt = Dates.nowIso(),
                                    status = Status.UNREAD,
                                ),
                            ),
                        )
                    }
                }.awaitAll()
            }
        } catch (_: Exception) {
            // notifications are a convenience; ignore failures
        }
    }

    /** Roster for a date with any attendance already recorded that day. */
    suspend fun attendanceSheet(classId: Long, date: String): List<AttendanceRow> = coroutineScope {
        api.studentClasses(classId = classId).sortedBy { it.student?.lastName ?: "" }.map { enrollment ->
            async {
                val existing = api.attendance(enrollment.id!!).firstOrNull { it.attendanceDate?.take(10) == date }
                AttendanceRow(enrollment, existing)
            }
        }.awaitAll()
    }

    suspend fun saveAttendance(date: String, rows: List<Pair<AttendanceRow, AttendanceStatus>>) = coroutineScope {
        rows.map { (row, status) ->
            async {
                val existing = row.existing
                if (existing == null) {
                    api.createAttendance(
                        Attendance(
                            studentClass = StudentClass(id = row.enrollment.id),
                            attendanceDate = date,
                            attendanceStatus = status,
                        ),
                    )
                } else if (existing.attendanceStatus != status) {
                    api.updateAttendance(existing.id!!, existing.copy(attendanceStatus = status))
                } else {
                    existing
                }
            }
        }.awaitAll()
    }

    suspend fun enrollmentRecord(enrollmentId: Long): EnrollmentRecord =
        enrollmentRecord(api.studentClass(enrollmentId))

    suspend fun homework(id: Long): Homework = api.homeworkById(id)

    suspend fun mission(id: Long): FamilyLearningMission = api.familyMission(id)

    suspend fun enrollmentRecord(enrollment: StudentClass): EnrollmentRecord = coroutineScope {
        val grades = async { api.grades(enrollment.id!!) }
        val attendance = async { api.attendance(enrollment.id!!) }
        EnrollmentRecord(
            enrollment = enrollment,
            grades = grades.await().sortedByDescending { it.gradeDate ?: "" },
            attendance = attendance.await().sortedByDescending { it.attendanceDate ?: "" },
        )
    }

    suspend fun addGrade(enrollmentId: Long, assessment: String, score: Double, maxScore: Double, remarks: String) =
        api.createGrade(
            Grade(
                studentClass = StudentClass(id = enrollmentId),
                assessmentName = assessment,
                score = score,
                maxScore = maxScore,
                gradeDate = Dates.todayIso(),
                remarks = remarks.ifBlank { null },
            ),
        )

    suspend fun submissions(homeworkId: Long): List<HomeworkSubmission> =
        api.homeworkSubmissions(homeworkId = homeworkId).sortedByDescending { it.submittedAt ?: "" }

    suspend fun gradeSubmission(submission: HomeworkSubmission, score: Double?, feedback: String): HomeworkSubmission {
        val updated = api.updateHomeworkSubmission(
            submission.id!!,
            submission.copy(score = score, feedback = feedback.ifBlank { null }, status = Status.GRADED),
        )
        submission.student?.id?.let { studentId ->
            try {
                api.createNotification(
                    Notification(
                        user = User(id = studentId),
                        homework = Homework(id = submission.homework?.id),
                        notificationType = "FEEDBACK",
                        message = "Your work on \"${submission.homework?.title ?: "homework"}\" was reviewed.",
                        deliveryMethod = DeliveryMethod.PUSH,
                        sentAt = Dates.nowIso(),
                        status = Status.UNREAD,
                    ),
                )
            } catch (_: Exception) {
            }
        }
        return updated
    }

    suspend fun homeworkReview(homeworkId: Long): HomeworkReview = coroutineScope {
        val homework = api.homeworkById(homeworkId)
        val submissions = async { submissions(homeworkId) }
        val roster = async { homework.schoolClass?.id?.let { api.studentClasses(classId = it) }.orEmpty() }
        HomeworkReview(homework, submissions.await(), roster.await())
    }

    suspend fun missionReview(missionId: Long): MissionReview = coroutineScope {
        val mission = api.familyMission(missionId)
        val participations = async { api.missionParticipations(missionId = missionId) }
        val roster = async { mission.schoolClass?.id?.let { api.studentClasses(classId = it) }.orEmpty() }
        MissionReview(mission, participations.await().sortedByDescending { it.completedAt ?: "" }, roster.await())
    }

    suspend fun participations(missionId: Long): List<MissionParticipation> =
        api.missionParticipations(missionId = missionId)

    /** Teacher acknowledges a completed mission and awards its points to the student. */
    suspend fun reviewParticipation(participation: MissionParticipation, feedback: String, awardPoints: Boolean) {
        api.updateMissionParticipation(
            participation.id!!,
            participation.copy(teacherFeedback = feedback.ifBlank { "Well done!" }),
        )
        val mission = participation.mission
        val studentId = participation.parentStudent?.student?.id
        if (awardPoints && mission?.points != null && studentId != null) {
            api.createPointTransaction(
                PointTransaction(
                    user = User(id = studentId),
                    mission = FamilyLearningMission(id = mission.id),
                    points = mission.points,
                    reason = "Family mission: ${mission.title}",
                    earnedAt = Dates.nowIso(),
                ),
            )
        }
    }

    // ======================= Student & Parent =======================

    suspend fun studentOverview(student: User): StudentOverview = coroutineScope {
        val studentId = student.id!!
        val enrollments = api.studentClasses(studentId = studentId)
        val classIds = enrollments.mapNotNull { it.schoolClass?.id }.distinct()

        val records = enrollments.map { async { enrollmentRecord(it) } }
        val lessons = classIds.map { async { api.lessons(it) } }
        val homework = classIds.map { async { api.homework(it) } }
        val missions = classIds.map { async { api.familyMissions(it) } }
        val announcements = classIds.map { async { api.announcements(it) } }
        val submissions = async { api.homeworkSubmissions(studentId = studentId) }
        val progress = async { api.lessonProgress(studentId = studentId) }
        val points = async { api.pointTransactions(userId = studentId) }
        val badges = async { api.userBadges(studentId) }

        StudentOverview(
            student = student,
            records = records.awaitAll(),
            lessons = lessons.awaitAll().flatten().sortedByDescending { it.publishedAt ?: "" },
            homework = homework.awaitAll().flatten().sortedBy { it.dueDate ?: "9999" },
            missions = missions.awaitAll().flatten().sortedByDescending { it.createdAt ?: "" },
            announcements = announcements.awaitAll().flatten().sortedByDescending { it.createdAt ?: "" },
            submissions = submissions.await(),
            lessonProgress = progress.await(),
            points = points.await().sortedByDescending { it.earnedAt ?: "" },
            badges = badges.await(),
        )
    }

    suspend fun submitHomework(homework: Homework, student: User, content: String, existing: HomeworkSubmission?) =
        if (existing == null) {
            api.createHomeworkSubmission(
                HomeworkSubmission(
                    homework = Homework(id = homework.id),
                    student = User(id = student.id),
                    submissionContent = content,
                    submittedAt = Dates.nowIso(),
                    status = Status.SUBMITTED,
                ),
            )
        } else {
            api.updateHomeworkSubmission(
                existing.id!!,
                existing.copy(submissionContent = content, submittedAt = Dates.nowIso(), status = Status.SUBMITTED),
            )
        }

    suspend fun completeLesson(lesson: Lesson, student: User, existing: LessonProgress?) =
        if (existing == null) {
            api.createLessonProgress(
                LessonProgress(
                    lesson = Lesson(id = lesson.id),
                    student = User(id = student.id),
                    status = Status.COMPLETED,
                    completedAt = Dates.nowIso(),
                ),
            )
        } else {
            api.updateLessonProgress(existing.id!!, existing.copy(status = Status.COMPLETED, completedAt = Dates.nowIso()))
        }

    suspend fun children(parentId: Long): List<ParentStudent> = api.parentStudents(parentId = parentId)

    /** Parents link a child by the email the child signed up with. */
    suspend fun linkChild(parent: User, childEmail: String, relationship: String): ParentStudent {
        val email = childEmail.trim().lowercase()
        val student = api.users(Role.STUDENT).firstOrNull { it.email?.lowercase() == email }
            ?: error("No student account uses $email. Ask your child to sign up first.")
        if (api.parentStudents(parentId = parent.id).any { it.student?.id == student.id }) {
            error("${student.fullName} is already linked to your account.")
        }
        return api.createParentStudent(
            ParentStudent(
                parent = User(id = parent.id),
                student = User(id = student.id),
                relationshipType = relationship.ifBlank { "Parent" },
            ),
        )
    }

    suspend fun missionsForChild(link: ParentStudent): List<MissionWithProgress> = coroutineScope {
        val studentId = link.student?.id ?: return@coroutineScope emptyList()
        val classIds = api.studentClasses(studentId = studentId).mapNotNull { it.schoolClass?.id }.distinct()
        val missions = classIds.map { async { api.familyMissions(it) } }
        val participations = async { api.missionParticipations(parentStudentId = link.id) }
        val done = participations.await()
        missions.awaitAll().flatten().sortedBy { it.dueDate ?: "9999" }.map { m ->
            MissionWithProgress(m, done.firstOrNull { it.mission?.id == m.id })
        }
    }

    /** Parent confirms the family finished a mission; the class teacher gets a notification to review it. */
    suspend fun completeMission(link: ParentStudent, mission: FamilyLearningMission): MissionParticipation {
        val created = api.createMissionParticipation(
            MissionParticipation(
                mission = FamilyLearningMission(id = mission.id),
                parentStudent = ParentStudent(id = link.id),
                completedAt = Dates.nowIso(),
                completionStatus = Status.COMPLETED,
                parentConfirmation = true,
            ),
        )
        mission.schoolClass?.teacher?.id?.let { teacherId ->
            try {
                api.createNotification(
                    Notification(
                        user = User(id = teacherId),
                        mission = FamilyLearningMission(id = mission.id),
                        notificationType = "MISSION",
                        message = "${link.student?.fullName ?: "A family"} completed \"${mission.title}\"",
                        deliveryMethod = DeliveryMethod.PUSH,
                        sentAt = Dates.nowIso(),
                        status = Status.UNREAD,
                    ),
                )
            } catch (_: Exception) {
            }
        }
        return created
    }
}
