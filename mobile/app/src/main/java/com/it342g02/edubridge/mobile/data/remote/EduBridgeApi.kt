package com.it342g02.edubridge.mobile.data.remote

import com.it342g02.edubridge.mobile.data.model.Announcement
import com.it342g02.edubridge.mobile.data.model.Attendance
import com.it342g02.edubridge.mobile.data.model.AuthResponse
import com.it342g02.edubridge.mobile.data.model.Badge
import com.it342g02.edubridge.mobile.data.model.FamilyLearningMission
import com.it342g02.edubridge.mobile.data.model.Grade
import com.it342g02.edubridge.mobile.data.model.Homework
import com.it342g02.edubridge.mobile.data.model.HomeworkSubmission
import com.it342g02.edubridge.mobile.data.model.LearningMaterial
import com.it342g02.edubridge.mobile.data.model.Lesson
import com.it342g02.edubridge.mobile.data.model.LessonProgress
import com.it342g02.edubridge.mobile.data.model.LoginRequest
import com.it342g02.edubridge.mobile.data.model.MissionParticipation
import com.it342g02.edubridge.mobile.data.model.Notification
import com.it342g02.edubridge.mobile.data.model.ParentStudent
import com.it342g02.edubridge.mobile.data.model.PointTransaction
import com.it342g02.edubridge.mobile.data.model.Role
import com.it342g02.edubridge.mobile.data.model.SchoolClass
import com.it342g02.edubridge.mobile.data.model.SignupRequest
import com.it342g02.edubridge.mobile.data.model.StudentClass
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.data.model.UserBadge
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit mirror of the web client's src/api/index.js. Paths are relative to the
 * base URL, which already ends in /api/. Every list filter is optional, just like the
 * Spring controllers' @RequestParam(required = false) parameters.
 */
interface EduBridgeApi {

    // ---- Auth ----
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): AuthResponse

    @GET("auth/me")
    suspend fun me(): User

    // ---- Users ----
    @GET("users")
    suspend fun users(@Query("role") role: Role? = null): List<User>

    @GET("users/{id}")
    suspend fun user(@Path("id") id: Long): User

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body body: User): User

    // ---- Classes ----
    @GET("classes")
    suspend fun classes(@Query("teacherId") teacherId: Long? = null): List<SchoolClass>

    @GET("classes/{id}")
    suspend fun schoolClass(@Path("id") id: Long): SchoolClass

    @POST("classes")
    suspend fun createClass(@Body body: SchoolClass): SchoolClass

    @PUT("classes/{id}")
    suspend fun updateClass(@Path("id") id: Long, @Body body: SchoolClass): SchoolClass

    @DELETE("classes/{id}")
    suspend fun deleteClass(@Path("id") id: Long): Response<Unit>

    // ---- Enrollments ----
    @GET("student-classes")
    suspend fun studentClasses(
        @Query("classId") classId: Long? = null,
        @Query("studentId") studentId: Long? = null,
    ): List<StudentClass>

    @GET("student-classes/{id}")
    suspend fun studentClass(@Path("id") id: Long): StudentClass

    @POST("student-classes")
    suspend fun createStudentClass(@Body body: StudentClass): StudentClass

    @DELETE("student-classes/{id}")
    suspend fun deleteStudentClass(@Path("id") id: Long): Response<Unit>

    // ---- Parent ↔ student links ----
    @GET("parent-students")
    suspend fun parentStudents(
        @Query("parentId") parentId: Long? = null,
        @Query("studentId") studentId: Long? = null,
    ): List<ParentStudent>

    @POST("parent-students")
    suspend fun createParentStudent(@Body body: ParentStudent): ParentStudent

    @DELETE("parent-students/{id}")
    suspend fun deleteParentStudent(@Path("id") id: Long): Response<Unit>

    // ---- Lessons ----
    @GET("lessons")
    suspend fun lessons(@Query("classId") classId: Long? = null): List<Lesson>

    @POST("lessons")
    suspend fun createLesson(@Body body: Lesson): Lesson

    @PUT("lessons/{id}")
    suspend fun updateLesson(@Path("id") id: Long, @Body body: Lesson): Lesson

    @DELETE("lessons/{id}")
    suspend fun deleteLesson(@Path("id") id: Long): Response<Unit>

    @GET("lesson-progress")
    suspend fun lessonProgress(
        @Query("lessonId") lessonId: Long? = null,
        @Query("studentId") studentId: Long? = null,
    ): List<LessonProgress>

    @POST("lesson-progress")
    suspend fun createLessonProgress(@Body body: LessonProgress): LessonProgress

    @PUT("lesson-progress/{id}")
    suspend fun updateLessonProgress(@Path("id") id: Long, @Body body: LessonProgress): LessonProgress

    @GET("learning-materials")
    suspend fun learningMaterials(@Query("lessonId") lessonId: Long? = null): List<LearningMaterial>

    @POST("learning-materials")
    suspend fun createLearningMaterial(@Body body: LearningMaterial): LearningMaterial

    @DELETE("learning-materials/{id}")
    suspend fun deleteLearningMaterial(@Path("id") id: Long): Response<Unit>

    // ---- Homework ----
    @GET("homework")
    suspend fun homework(@Query("classId") classId: Long? = null): List<Homework>

    @GET("homework/{id}")
    suspend fun homeworkById(@Path("id") id: Long): Homework

    @POST("homework")
    suspend fun createHomework(@Body body: Homework): Homework

    @PUT("homework/{id}")
    suspend fun updateHomework(@Path("id") id: Long, @Body body: Homework): Homework

    @DELETE("homework/{id}")
    suspend fun deleteHomework(@Path("id") id: Long): Response<Unit>

    @GET("homework-submissions")
    suspend fun homeworkSubmissions(
        @Query("homeworkId") homeworkId: Long? = null,
        @Query("studentId") studentId: Long? = null,
    ): List<HomeworkSubmission>

    @POST("homework-submissions")
    suspend fun createHomeworkSubmission(@Body body: HomeworkSubmission): HomeworkSubmission

    @PUT("homework-submissions/{id}")
    suspend fun updateHomeworkSubmission(
        @Path("id") id: Long,
        @Body body: HomeworkSubmission,
    ): HomeworkSubmission

    // ---- Announcements & notifications ----
    @GET("announcements")
    suspend fun announcements(@Query("classId") classId: Long? = null): List<Announcement>

    @POST("announcements")
    suspend fun createAnnouncement(@Body body: Announcement): Announcement

    @DELETE("announcements/{id}")
    suspend fun deleteAnnouncement(@Path("id") id: Long): Response<Unit>

    @GET("notifications")
    suspend fun notifications(@Query("userId") userId: Long? = null): List<Notification>

    @POST("notifications")
    suspend fun createNotification(@Body body: Notification): Notification

    @PUT("notifications/{id}")
    suspend fun updateNotification(@Path("id") id: Long, @Body body: Notification): Notification

    // ---- Family learning missions ----
    @GET("family-missions")
    suspend fun familyMissions(@Query("classId") classId: Long? = null): List<FamilyLearningMission>

    @GET("family-missions/{id}")
    suspend fun familyMission(@Path("id") id: Long): FamilyLearningMission

    @POST("family-missions")
    suspend fun createFamilyMission(@Body body: FamilyLearningMission): FamilyLearningMission

    @PUT("family-missions/{id}")
    suspend fun updateFamilyMission(
        @Path("id") id: Long,
        @Body body: FamilyLearningMission,
    ): FamilyLearningMission

    @DELETE("family-missions/{id}")
    suspend fun deleteFamilyMission(@Path("id") id: Long): Response<Unit>

    @GET("mission-participations")
    suspend fun missionParticipations(
        @Query("missionId") missionId: Long? = null,
        @Query("parentStudentId") parentStudentId: Long? = null,
    ): List<MissionParticipation>

    @POST("mission-participations")
    suspend fun createMissionParticipation(@Body body: MissionParticipation): MissionParticipation

    @PUT("mission-participations/{id}")
    suspend fun updateMissionParticipation(
        @Path("id") id: Long,
        @Body body: MissionParticipation,
    ): MissionParticipation

    // ---- Points & badges ----
    @GET("point-transactions")
    suspend fun pointTransactions(
        @Query("userId") userId: Long? = null,
        @Query("missionId") missionId: Long? = null,
    ): List<PointTransaction>

    @POST("point-transactions")
    suspend fun createPointTransaction(@Body body: PointTransaction): PointTransaction

    @GET("badges")
    suspend fun badges(): List<Badge>

    @GET("user-badges")
    suspend fun userBadges(@Query("userId") userId: Long? = null): List<UserBadge>

    // ---- Attendance & grades ----
    @GET("attendance")
    suspend fun attendance(@Query("studentClassId") studentClassId: Long? = null): List<Attendance>

    @POST("attendance")
    suspend fun createAttendance(@Body body: Attendance): Attendance

    @PUT("attendance/{id}")
    suspend fun updateAttendance(@Path("id") id: Long, @Body body: Attendance): Attendance

    @GET("grades")
    suspend fun grades(@Query("studentClassId") studentClassId: Long? = null): List<Grade>

    @POST("grades")
    suspend fun createGrade(@Body body: Grade): Grade

    @DELETE("grades/{id}")
    suspend fun deleteGrade(@Path("id") id: Long): Response<Unit>
}
