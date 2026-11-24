package com.it342g02.edubridge.mobile.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.Homework
import com.it342g02.edubridge.mobile.data.model.Lesson
import com.it342g02.edubridge.mobile.data.model.Status
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.data.repository.StudentOverview
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.FormDialog
import com.it342g02.edubridge.mobile.ui.components.LinkButton
import com.it342g02.edubridge.mobile.ui.components.ListItemRow
import com.it342g02.edubridge.mobile.ui.components.Load
import com.it342g02.edubridge.mobile.ui.components.LoadedList
import com.it342g02.edubridge.mobile.ui.components.Loader
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PageHeading
import com.it342g02.edubridge.mobile.ui.components.ProgressRow
import com.it342g02.edubridge.mobile.ui.components.SecondaryButton
import com.it342g02.edubridge.mobile.ui.components.SectionHeader
import com.it342g02.edubridge.mobile.ui.components.StatGrid
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.components.rememberActionRunner
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.navigation.Routes
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.util.Dates

@Composable
fun StudentHomeScreen(user: User, overview: Loader<StudentOverview>, onOpen: (String) -> Unit) {
    LoadedList(overview) { o ->
        item {
            PageHeading(
                eyebrow = Dates.todayEyebrow(),
                title = "${Dates.greeting()}, ${user.firstName}!",
                description = "Here's what's happening in your classes.",
            )
        }

        item {
            StatGrid(
                listOf(
                    Triple("My Classes", o.classes.size.toString(), Icons.Outlined.School),
                    Triple("Homework to do", o.pendingHomework.size.toString(), Icons.AutoMirrored.Outlined.Assignment),
                    Triple("Points", o.totalPoints.toString(), Icons.Outlined.Stars),
                    Triple("Badges", o.badges.size.toString(), Icons.Outlined.EmojiEvents),
                ),
            )
        }

        item {
            EduCard {
                SectionHeader("Due soon", "Homework you still need to submit") {
                    LinkButton("See all") { onOpen(Routes.S_HOMEWORK) }
                }
                val due = o.pendingHomework.take(3)
                if (due.isEmpty()) {
                    EmptyState(Icons.Outlined.CheckCircle, "You're all caught up!", "No homework waiting for you.")
                }
                due.forEach { hw ->
                    ListItemRow(
                        title = hw.title ?: "Homework",
                        subtitle = hw.schoolClass?.className,
                        icon = Icons.AutoMirrored.Outlined.Assignment,
                        onClick = { onOpen(Routes.S_HOMEWORK) },
                    ) {
                        StatusChip(Dates.dueLabel(hw.dueDate), if (Dates.isPast(hw.dueDate)) ChipTone.Danger else ChipTone.Primary)
                    }
                }
            }
        }

        item {
            EduCard {
                SectionHeader("My progress") { LinkButton("Details") { onOpen(Routes.S_PROGRESS) } }
                ProgressRow("Average grade", o.averageGrade)
                ProgressRow("Attendance", o.attendanceRate)
                val done = o.lessons.count { o.isLessonDone(it) }
                ProgressRow(
                    "Lessons completed ($done of ${o.lessons.size})",
                    if (o.lessons.isEmpty()) null else done * 100.0 / o.lessons.size,
                )
            }
        }

        item { AnnouncementsCard(o) }
    }
}

@Composable
fun StudentHomeworkScreen(user: User, overview: Loader<StudentOverview>) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    var showSubmitted by rememberSaveable { mutableStateOf(false) }
    var submittingId by rememberSaveable { mutableStateOf<Long?>(null) }

    LoadedList(overview) { o ->
        item {
            PageHeading("SCHOOLWORK", "Homework", "Submit your work and see your teacher's feedback.")
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleChip("To do (${o.pendingHomework.size})", !showSubmitted) { showSubmitted = false }
                ToggleChip("Submitted (${o.homework.size - o.pendingHomework.size})", showSubmitted) { showSubmitted = true }
            }
        }

        val list = if (showSubmitted) o.homework.filter { o.submissionFor(it) != null } else o.pendingHomework
        if (list.isEmpty()) {
            item {
                EduCard {
                    EmptyState(
                        Icons.AutoMirrored.Outlined.Assignment,
                        if (showSubmitted) "Nothing submitted yet" else "No homework to do",
                    )
                }
            }
        }

        items(list, key = { it.id!! }) { hw ->
            val submission = o.submissionFor(hw)
            EduCard {
                Text(hw.schoolClass?.className?.uppercase() ?: "", style = MaterialTheme.typography.labelSmall)
                Text(hw.title ?: "Homework", style = MaterialTheme.typography.titleMedium)
                hw.description?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (submission == null) {
                        StatusChip(Dates.dueLabel(hw.dueDate), if (Dates.isPast(hw.dueDate)) ChipTone.Danger else ChipTone.Primary)
                    } else if (submission.status.equals(Status.GRADED, true)) {
                        StatusChip("Reviewed", ChipTone.Success)
                        submission.score?.let { StatusChip("Score: ${"%.0f".format(it)}", ChipTone.Success) }
                    } else {
                        StatusChip("Submitted ${Dates.formatDate(submission.submittedAt)}", ChipTone.Primary)
                    }
                }
                submission?.feedback?.let {
                    Spacer(Modifier.height(8.dp))
                    Text("Teacher feedback", style = MaterialTheme.typography.labelSmall)
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                if (submission == null || !submission.status.equals(Status.GRADED, true)) {
                    Spacer(Modifier.height(10.dp))
                    SecondaryButton(
                        if (submission == null) "Submit work" else "Edit submission",
                        { submittingId = hw.id },
                        Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    val o = (overview.state as? Load.Ready)?.data
    val hw: Homework? = o?.homework?.firstOrNull { it.id == submittingId }
    if (o != null && hw != null) {
        val existing = o.submissionFor(hw)
        var text by rememberSaveable(hw.id) { mutableStateOf(existing?.submissionContent ?: "") }
        FormDialog(
            title = hw.title ?: "Submit homework",
            confirmText = "Submit",
            busy = actions.busy,
            confirmEnabled = text.isNotBlank(),
            onDismiss = { submittingId = null },
            onConfirm = {
                actions.run("Homework submitted", onSuccess = { submittingId = null; overview.reload() }) {
                    repo.submitHomework(hw, user, text.trim(), existing)
                }
            },
        ) {
            Text(Dates.dueLabel(hw.dueDate), style = MaterialTheme.typography.bodySmall)
            EduTextField("Your answer", text, { text = it }, singleLine = false, minLines = 6, placeholder = "Type your work or paste a link")
        }
    }
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = EduColors.PrimaryLight,
            selectedLabelColor = EduColors.PrimaryDark,
            containerColor = EduColors.Surface,
            labelColor = EduColors.TextMuted,
        ),
    )
}

@Composable
fun StudentLessonsScreen(user: User, overview: Loader<StudentOverview>) {
    var openId by rememberSaveable { mutableStateOf<Long?>(null) }

    LoadedList(overview) { o ->
        item { PageHeading("LEARNING", "Lessons", "Read lessons from your classes and mark them complete.") }

        if (o.lessons.isEmpty()) {
            item { EduCard { EmptyState(Icons.AutoMirrored.Outlined.MenuBook, "No lessons yet", "Lessons your teachers publish appear here.") } }
        }

        o.lessons.groupBy { it.schoolClass?.className ?: "Class" }.forEach { (className, lessons) ->
            item { SectionHeader(className, "${lessons.count { o.isLessonDone(it) }} of ${lessons.size} completed") }
            items(lessons, key = { it.id!! }) { lesson ->
                EduCard(onClick = { openId = lesson.id }) {
                    ListItemRow(
                        title = lesson.title ?: "Lesson",
                        subtitle = lesson.learningObjective ?: lesson.description,
                        icon = Icons.AutoMirrored.Outlined.MenuBook,
                    ) {
                        if (o.isLessonDone(lesson)) StatusChip("Done", ChipTone.Success) else StatusChip("New", ChipTone.Primary)
                    }
                }
            }
        }
    }

    val o = (overview.state as? Load.Ready)?.data
    val lesson = o?.lessons?.firstOrNull { it.id == openId }
    if (o != null && lesson != null) {
        LessonDialog(user, o, lesson, onDismiss = { openId = null }, onCompleted = { openId = null; overview.reload() })
    }
}

@Composable
private fun LessonDialog(user: User, o: StudentOverview, lesson: Lesson, onDismiss: () -> Unit, onCompleted: () -> Unit) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val materials = rememberLoader(lesson.id) { repo.materials(lesson.id!!) }
    val done = o.isLessonDone(lesson)

    FormDialog(
        title = lesson.title ?: "Lesson",
        confirmText = if (done) "Close" else "Mark complete",
        busy = actions.busy,
        onDismiss = onDismiss,
        onConfirm = {
            if (done) {
                onDismiss()
            } else {
                actions.run("Lesson completed!", onSuccess = onCompleted) {
                    repo.completeLesson(lesson, user, o.lessonProgress.firstOrNull { it.lesson?.id == lesson.id })
                }
            }
        },
    ) {
        lesson.learningObjective?.let {
            Text("OBJECTIVE", style = MaterialTheme.typography.labelSmall)
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }
        lesson.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = EduColors.TextMuted) }
        lesson.content?.let { Text(it, style = MaterialTheme.typography.bodyLarge) }

        val list = materials.data.orEmpty()
        if (list.isNotEmpty()) {
            Text("LEARNING MATERIALS", style = MaterialTheme.typography.labelSmall)
            list.forEach { m ->
                ListItemRow(
                    title = m.materialName ?: "Material",
                    subtitle = listOfNotNull(m.materialType, m.fileUrl).joinToString(" · "),
                    icon = Icons.Outlined.Folder,
                )
            }
        }
        if (done) StatusChip("You completed this lesson", ChipTone.Success)
    }
}

@Composable
fun StudentProgressScreen(overview: Loader<StudentOverview>) {
    LoadedList(overview) { o ->
        item { PageHeading("ACADEMIC", "My Progress", "Your grades, attendance and rewards.") }
        progressSections(o)
    }
}
