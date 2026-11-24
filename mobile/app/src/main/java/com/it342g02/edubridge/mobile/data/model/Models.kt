package com.it342g02.edubridge.mobile.data.model

/*
 * Kotlin mirrors of the Spring Boot entities in
 * backend/edubridge/src/main/java/com/it342g02/edubridge/edubridge/entity.
 *
 * Every field is nullable with a default because Gson fills objects by reflection and
 * the API omits or nulls fields freely. Dates arrive as ISO-8601 strings
 * (LocalDateTime "2026-11-24T09:30:00", LocalDate "2026-11-24"); see util/Dates.kt.
 *
 * To reference an existing row in a request body, send just its id, e.g.
 * Homework(schoolClass = SchoolClass(id = 3)). Gson skips nulls, so the JSON is
 * {"schoolClass":{"id":3}}, which is what the JPA @ManyToOne expects.
 *
 * PUT replaces the whole row on the server, so updates must send a full copy of the
 * object (fetched first) rather than only the changed fields.
 */

enum class Role { TEACHER, PARENT, STUDENT }

enum class AttendanceStatus { PRESENT, ABSENT, LATE, EXCUSED }

enum class DeliveryMethod { PUSH, EMAIL, SMS }

data class User(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val role: Role? = null,
    val phoneNumber: String? = null,
    val preferredLanguage: String? = null,
    val createdAt: String? = null,
) {
    val fullName: String get() = listOfNotNull(firstName, lastName).joinToString(" ")
    val initials: String
        get() = "${firstName?.firstOrNull() ?: ""}${lastName?.firstOrNull() ?: ""}".uppercase()
    val roleLabel: String
        get() = role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
}

data class SchoolClass(
    val id: Long? = null,
    val teacher: User? = null,
    val className: String? = null,
    val gradeLevel: String? = null,
    val schoolYear: String? = null,
    val createdAt: String? = null,
)

data class StudentClass(
    val id: Long? = null,
    val student: User? = null,
    val schoolClass: SchoolClass? = null,
    val enrollmentDate: String? = null,
)

data class ParentStudent(
    val id: Long? = null,
    val parent: User? = null,
    val student: User? = null,
    val relationshipType: String? = null,
)

data class Lesson(
    val id: Long? = null,
    val schoolClass: SchoolClass? = null,
    val title: String? = null,
    val description: String? = null,
    val content: String? = null,
    val learningObjective: String? = null,
    val publishedAt: String? = null,
    val status: String? = null,
)

data class LessonProgress(
    val id: Long? = null,
    val lesson: Lesson? = null,
    val student: User? = null,
    val status: String? = null,
    val completedAt: String? = null,
)

data class LearningMaterial(
    val id: Long? = null,
    val lesson: Lesson? = null,
    val materialName: String? = null,
    val materialType: String? = null,
    val fileUrl: String? = null,
    val createdAt: String? = null,
)

data class Homework(
    val id: Long? = null,
    val schoolClass: SchoolClass? = null,
    val title: String? = null,
    val description: String? = null,
    val dueDate: String? = null,
    val createdAt: String? = null,
    val status: String? = null,
)

data class HomeworkSubmission(
    val id: Long? = null,
    val homework: Homework? = null,
    val student: User? = null,
    val submissionContent: String? = null,
    val submittedAt: String? = null,
    val status: String? = null,
    val score: Double? = null,
    val feedback: String? = null,
)

data class Announcement(
    val id: Long? = null,
    val schoolClass: SchoolClass? = null,
    val title: String? = null,
    val message: String? = null,
    val createdAt: String? = null,
    val priority: String? = null,
)

data class Notification(
    val id: Long? = null,
    val user: User? = null,
    val announcement: Announcement? = null,
    val homework: Homework? = null,
    val mission: FamilyLearningMission? = null,
    val notificationType: String? = null,
    val message: String? = null,
    val deliveryMethod: DeliveryMethod? = null,
    val sentAt: String? = null,
    val status: String? = null,
) {
    val isUnread: Boolean get() = status.equals(Status.UNREAD, ignoreCase = true)
}

data class FamilyLearningMission(
    val id: Long? = null,
    val schoolClass: SchoolClass? = null,
    val title: String? = null,
    val description: String? = null,
    val learningObjective: String? = null,
    val instructions: String? = null,
    val dueDate: String? = null,
    val points: Int? = null,
    val createdAt: String? = null,
    val status: String? = null,
)

data class MissionParticipation(
    val id: Long? = null,
    val mission: FamilyLearningMission? = null,
    val parentStudent: ParentStudent? = null,
    val completedAt: String? = null,
    val completionStatus: String? = null,
    val parentConfirmation: Boolean? = null,
    val teacherFeedback: String? = null,
)

data class PointTransaction(
    val id: Long? = null,
    val user: User? = null,
    val mission: FamilyLearningMission? = null,
    val points: Int? = null,
    val reason: String? = null,
    val earnedAt: String? = null,
)

data class Badge(
    val id: Long? = null,
    val badgeName: String? = null,
    val description: String? = null,
    val badgeType: String? = null,
    val requiredPoints: Int? = null,
    val createdAt: String? = null,
)

data class UserBadge(
    val id: Long? = null,
    val user: User? = null,
    val badge: Badge? = null,
    val awardedAt: String? = null,
)

data class Attendance(
    val id: Long? = null,
    val studentClass: StudentClass? = null,
    val attendanceDate: String? = null,
    val attendanceStatus: AttendanceStatus? = null,
    val remarks: String? = null,
)

data class Grade(
    val id: Long? = null,
    val studentClass: StudentClass? = null,
    val assessmentName: String? = null,
    val score: Double? = null,
    val maxScore: Double? = null,
    val gradeDate: String? = null,
    val remarks: String? = null,
) {
    val percent: Double?
        get() = if (score != null && maxScore != null && maxScore > 0) score / maxScore * 100 else null
}

// ---- Auth DTOs (backend dto package) ----

data class LoginRequest(val email: String, val password: String)

data class SignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: Role,
    val phoneNumber: String? = null,
    val preferredLanguage: String? = null,
)

data class AuthResponse(val token: String? = null, val user: User? = null)

/**
 * The backend stores these status columns as free text. The mobile app writes these
 * values so the web app and future reports can rely on a consistent vocabulary.
 */
object Status {
    const val UNREAD = "UNREAD"
    const val READ = "READ"
    const val ACTIVE = "ACTIVE"
    const val PUBLISHED = "PUBLISHED"
    const val ASSIGNED = "ASSIGNED"
    const val SUBMITTED = "SUBMITTED"
    const val GRADED = "GRADED"
    const val COMPLETED = "COMPLETED"
    const val NORMAL = "NORMAL"
    const val HIGH = "HIGH"
}
