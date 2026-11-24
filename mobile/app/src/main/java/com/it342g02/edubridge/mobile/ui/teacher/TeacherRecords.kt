package com.it342g02.edubridge.mobile.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.AttendanceStatus
import com.it342g02.edubridge.mobile.data.model.HomeworkSubmission
import com.it342g02.edubridge.mobile.data.model.MissionParticipation
import com.it342g02.edubridge.mobile.data.model.Status
import com.it342g02.edubridge.mobile.ui.components.Avatar
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.DateField
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.FormDialog
import com.it342g02.edubridge.mobile.ui.components.ListItemRow
import com.it342g02.edubridge.mobile.ui.components.LoadedList
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PageHeading
import com.it342g02.edubridge.mobile.ui.components.PrimaryButton
import com.it342g02.edubridge.mobile.ui.components.ProgressRow
import com.it342g02.edubridge.mobile.ui.components.SecondaryButton
import com.it342g02.edubridge.mobile.ui.components.SectionHeader
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.components.rememberActionRunner
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.shared.AttendanceItem
import com.it342g02.edubridge.mobile.ui.shared.GradeItem
import com.it342g02.edubridge.mobile.ui.shared.label
import com.it342g02.edubridge.mobile.ui.shared.tone
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.ui.theme.EduRadius
import com.it342g02.edubridge.mobile.util.Dates
import java.time.LocalDate

// =====================================================================
// Attendance
// =====================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendanceScreen(classId: Long, onDone: () -> Unit) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    var date by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val loader = rememberLoader(classId, date) { repo.attendanceSheet(classId, date.toString()) }

    // working copy of each student's status, seeded from what is already saved for the day
    val statuses = remember(loader.data) {
        mutableStateMapOf<Long, AttendanceStatus>().apply {
            loader.data?.forEach { put(it.enrollment.id!!, it.existing?.attendanceStatus ?: AttendanceStatus.PRESENT) }
        }
    }

    LoadedList(loader) { rows ->
        item {
            PageHeading("ACADEMIC", "Attendance", "Tap a status for each student, then save.")
            Spacer(Modifier.height(12.dp))
            EduCard { DateField("Date", date) { picked -> if (picked != null) date = picked } }
        }

        if (rows.isEmpty()) {
            item { EduCard { EmptyState(Icons.Outlined.Groups, "No students enrolled", "Enroll students in this class first.") } }
        } else {
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AttendanceStatus.entries.forEach { s ->
                        StatusChip("${s.label()}: ${statuses.values.count { it == s }}", s.tone())
                    }
                }
            }
            item {
                SecondaryButton(
                    "Mark everyone present",
                    { rows.forEach { statuses[it.enrollment.id!!] = AttendanceStatus.PRESENT } },
                    Modifier.padding(0.dp),
                )
            }
        }

        items(rows, key = { it.enrollment.id!! }) { row ->
            val id = row.enrollment.id!!
            EduCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(row.enrollment.student?.initials ?: "", 32.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        row.enrollment.student?.fullName ?: "Student",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                    )
                    if (row.existing != null) StatusChip("Saved")
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AttendanceStatus.entries.forEach { s ->
                        StatusOption(
                            label = s.label(),
                            selected = statuses[id] == s,
                            modifier = Modifier.weight(1f),
                        ) { statuses[id] = s }
                    }
                }
            }
        }

        if (rows.isNotEmpty()) {
            item {
                PrimaryButton(
                    "Save attendance",
                    loading = actions.busy,
                    onClick = {
                        actions.run("Attendance saved", onSuccess = onDone) {
                            repo.saveAttendance(date.toString(), rows.map { it to (statuses[it.enrollment.id!!] ?: AttendanceStatus.PRESENT) })
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun StatusOption(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(EduRadius.Sm)
    Text(
        label,
        modifier = modifier
            .clip(shape)
            .background(if (selected) EduColors.Primary else EduColors.InputBackground)
            .border(1.dp, if (selected) EduColors.Primary else EduColors.Border, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        color = if (selected) androidx.compose.ui.graphics.Color.White else EduColors.TextMuted,
        style = MaterialTheme.typography.labelMedium,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

// =====================================================================
// One student's record in one class (grades + attendance)
// =====================================================================

@Composable
fun StudentRecordScreen(enrollmentId: Long) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val loader = rememberLoader(enrollmentId) { repo.enrollmentRecord(enrollmentId) }
    var addingGrade by rememberSaveable { mutableStateOf(false) }

    LoadedList(loader) { r ->
        val student = r.enrollment.student
        item {
            EduCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(student?.initials ?: "", 52.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(student?.fullName ?: "Student", style = MaterialTheme.typography.titleLarge)
                        Text(r.enrollment.schoolClass?.className ?: "", style = MaterialTheme.typography.bodySmall)
                        Text(student?.email ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(12.dp))
                val avg = r.grades.mapNotNull { it.percent }.takeIf { it.isNotEmpty() }?.average()
                val rate = r.attendance.takeIf { it.isNotEmpty() }
                    ?.let { list -> list.count { it.attendanceStatus != AttendanceStatus.ABSENT } * 100.0 / list.size }
                ProgressRow("Average grade", avg)
                ProgressRow("Attendance", rate)
            }
        }

        item {
            EduCard {
                SectionHeader("Grades", "${r.grades.size} recorded")
                Spacer(Modifier.height(4.dp))
                if (r.grades.isEmpty()) Text("No grades yet.", style = MaterialTheme.typography.bodySmall)
                r.grades.forEach { GradeItem(it) }
                Spacer(Modifier.height(8.dp))
                SecondaryButton("Record grade", { addingGrade = true }, icon = Icons.Outlined.Add)
            }
        }

        item {
            EduCard {
                SectionHeader("Attendance history")
                if (r.attendance.isEmpty()) {
                    EmptyState(Icons.Outlined.EventAvailable, "No attendance yet")
                }
                r.attendance.take(15).forEach { AttendanceItem(it) }
            }
        }
    }

    if (addingGrade) {
        var name by rememberSaveable { mutableStateOf("") }
        var score by rememberSaveable { mutableStateOf("") }
        var max by rememberSaveable { mutableStateOf("100") }
        var remarks by rememberSaveable { mutableStateOf("") }
        val scoreValue = score.toDoubleOrNull()
        val maxValue = max.toDoubleOrNull()
        FormDialog(
            title = "Record grade",
            confirmText = "Save",
            busy = actions.busy,
            confirmEnabled = name.isNotBlank() && scoreValue != null && maxValue != null && maxValue > 0,
            onDismiss = { addingGrade = false },
            onConfirm = {
                actions.run("Grade recorded", onSuccess = { addingGrade = false; loader.reload() }) {
                    repo.addGrade(enrollmentId, name.trim(), scoreValue!!, maxValue!!, remarks.trim())
                }
            },
        ) {
            EduTextField("Assessment", name, { name = it }, placeholder = "e.g. Quiz 3")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EduTextField("Score", score, { score = it }, Modifier.weight(1f), keyboardType = KeyboardType.Decimal)
                EduTextField("Out of", max, { max = it }, Modifier.weight(1f), keyboardType = KeyboardType.Decimal)
            }
            EduTextField("Remarks (optional)", remarks, { remarks = it })
        }
    }
}

// =====================================================================
// Homework submissions
// =====================================================================

@Composable
fun SubmissionsScreen(homeworkId: Long) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val loader = rememberLoader(homeworkId) { repo.homeworkReview(homeworkId) }
    var grading by remember { mutableStateOf<HomeworkSubmission?>(null) }

    LoadedList(loader) { r ->
        item {
            EduCard {
                Text(r.homework.schoolClass?.className?.uppercase() ?: "HOMEWORK", style = MaterialTheme.typography.labelSmall)
                Text(r.homework.title ?: "Homework", style = MaterialTheme.typography.titleLarge)
                r.homework.description?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip(Dates.dueLabel(r.homework.dueDate), ChipTone.Primary)
                    StatusChip("${r.submissions.size}/${r.roster.size} submitted", ChipTone.Success)
                }
            }
        }

        item { SectionHeader("Submissions") }
        if (r.submissions.isEmpty()) {
            item { EduCard { EmptyState(Icons.AutoMirrored.Outlined.Assignment, "No submissions yet") } }
        }
        items(r.submissions, key = { it.id!! }) { s ->
            EduCard(onClick = { grading = s }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(s.student?.initials ?: "", 32.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(s.student?.fullName ?: "Student", style = MaterialTheme.typography.titleSmall)
                        Text("Submitted ${Dates.formatDateTime(s.submittedAt)}", style = MaterialTheme.typography.bodySmall)
                    }
                    val graded = s.status.equals(Status.GRADED, true)
                    StatusChip(
                        if (graded) "Score: ${s.score?.let { "%.0f".format(it) } ?: "—"}" else "To review",
                        if (graded) ChipTone.Success else ChipTone.Warning,
                    )
                }
                s.submissionContent?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 4)
                }
                s.feedback?.let {
                    Spacer(Modifier.height(6.dp))
                    Text("Feedback: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (r.missing.isNotEmpty()) {
            item {
                EduCard {
                    SectionHeader("Not yet submitted", "${r.missing.size} students")
                    r.missing.forEach { e ->
                        ListItemRow(
                            title = e.student?.fullName ?: "Student",
                            icon = Icons.Outlined.HourglassEmpty,
                        )
                    }
                }
            }
        }
    }

    grading?.let { s ->
        var score by rememberSaveable(s.id) { mutableStateOf(s.score?.let { "%.0f".format(it) } ?: "") }
        var feedback by rememberSaveable(s.id) { mutableStateOf(s.feedback ?: "") }
        FormDialog(
            title = "Review ${s.student?.firstName ?: "submission"}",
            confirmText = "Save review",
            busy = actions.busy,
            onDismiss = { grading = null },
            onConfirm = {
                actions.run("Review saved", onSuccess = { grading = null; loader.reload() }) {
                    repo.gradeSubmission(s, score.toDoubleOrNull(), feedback.trim())
                }
            },
        ) {
            Text(s.submissionContent ?: "(No text submitted)", style = MaterialTheme.typography.bodyMedium)
            EduTextField("Score", score, { score = it }, keyboardType = KeyboardType.Decimal)
            EduTextField("Feedback", feedback, { feedback = it }, singleLine = false, minLines = 3)
        }
    }
}

// =====================================================================
// Family mission review
// =====================================================================

@Composable
fun MissionReviewScreen(missionId: Long) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val loader = rememberLoader(missionId) { repo.missionReview(missionId) }
    var reviewing by remember { mutableStateOf<MissionParticipation?>(null) }

    LoadedList(loader) { r ->
        val m = r.mission
        item {
            EduCard {
                Text("FAMILY LEARNING MISSION", style = MaterialTheme.typography.labelSmall)
                Text(m.title ?: "Mission", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("${m.points ?: 0} pts", ChipTone.Success)
                    StatusChip(Dates.dueLabel(m.dueDate), ChipTone.Primary)
                }
                m.description?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.bodyMedium) }
                m.learningObjective?.let {
                    Spacer(Modifier.height(10.dp))
                    Text("OBJECTIVE", style = MaterialTheme.typography.labelSmall)
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                m.instructions?.let {
                    Spacer(Modifier.height(10.dp))
                    Text("INSTRUCTIONS", style = MaterialTheme.typography.labelSmall)
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(10.dp))
                val families = r.participations.mapNotNull { it.parentStudent?.student?.id }.toSet().size
                ProgressRow(
                    "Families completed ($families of ${r.roster.size})",
                    if (r.roster.isEmpty()) null else families * 100.0 / r.roster.size,
                )
            }
        }

        item { SectionHeader("Completions") }
        if (r.participations.isEmpty()) {
            item { EduCard { EmptyState(Icons.Outlined.FavoriteBorder, "No families have completed this yet") } }
        }
        items(r.participations, key = { it.id!! }) { p ->
            val reviewed = !p.teacherFeedback.isNullOrBlank()
            EduCard(onClick = if (reviewed) null else ({ reviewing = p })) {
                ListItemRow(
                    title = p.parentStudent?.student?.fullName ?: "Student",
                    subtitle = "With ${p.parentStudent?.parent?.fullName ?: "parent"} · ${Dates.formatDateTime(p.completedAt)}",
                    leading = { Avatar(p.parentStudent?.student?.initials ?: "", 32.dp) },
                ) { StatusChip(if (reviewed) "Reviewed" else "Review", if (reviewed) ChipTone.Success else ChipTone.Warning) }
                p.teacherFeedback?.let { Text("Your feedback: $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }

    reviewing?.let { p ->
        var feedback by rememberSaveable(p.id) { mutableStateOf("") }
        var award by rememberSaveable(p.id) { mutableStateOf(true) }
        val points = (loader.data?.mission?.points) ?: 0
        FormDialog(
            title = "Review completion",
            confirmText = "Save",
            busy = actions.busy,
            onDismiss = { reviewing = null },
            onConfirm = {
                actions.run("Review saved", onSuccess = { reviewing = null; loader.reload() }) {
                    repo.reviewParticipation(p.copy(mission = loader.data?.mission ?: p.mission), feedback.trim(), award)
                }
            },
        ) {
            EduTextField("Feedback for the family", feedback, { feedback = it }, singleLine = false, minLines = 3)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = award,
                    onCheckedChange = { award = it },
                    colors = CheckboxDefaults.colors(checkedColor = EduColors.Primary),
                )
                Text("Award $points points to ${p.parentStudent?.student?.firstName ?: "the student"}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
