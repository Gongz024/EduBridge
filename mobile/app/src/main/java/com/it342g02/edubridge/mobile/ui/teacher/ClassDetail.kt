package com.it342g02.edubridge.mobile.ui.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.Lesson
import com.it342g02.edubridge.mobile.data.model.StudentClass
import com.it342g02.edubridge.mobile.ui.components.Avatar
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.ConfirmDialog
import com.it342g02.edubridge.mobile.ui.components.DateField
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.FormDialog
import com.it342g02.edubridge.mobile.ui.components.IconTile
import com.it342g02.edubridge.mobile.ui.components.ListItemRow
import com.it342g02.edubridge.mobile.ui.components.Load
import com.it342g02.edubridge.mobile.ui.components.LoadedList
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PrimaryButton
import com.it342g02.edubridge.mobile.ui.components.SecondaryButton
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.components.rememberActionRunner
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.util.Dates
import java.time.LocalDate

private enum class ClassTab(val label: String) {
    Students("Students"), Lessons("Lessons"), Homework("Homework"), Missions("Missions"), Posts("Announcements")
}

private enum class CreateKind { Enroll, Lesson, Homework, Mission, Announcement }

@Composable
fun ClassDetailScreen(
    classId: Long,
    onTakeAttendance: (Long) -> Unit,
    onOpenStudent: (Long) -> Unit,
    onOpenHomework: (Long) -> Unit,
    onOpenMission: (Long) -> Unit,
) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val loader = rememberLoader(classId) { repo.classDetail(classId) }

    var tab by rememberSaveable { mutableStateOf(ClassTab.Students) }
    var creating by rememberSaveable { mutableStateOf<CreateKind?>(null) }
    var openLessonId by rememberSaveable { mutableStateOf<Long?>(null) }
    var removing by remember { mutableStateOf<StudentClass?>(null) }

    LoadedList(loader) { d ->
        item {
            EduCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(Icons.Outlined.School, 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.schoolClass.className ?: "Class", style = MaterialTheme.typography.titleLarge)
                        Text(
                            listOfNotNull(d.schoolClass.gradeLevel, d.schoolClass.schoolYear).joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("${d.enrollments.size} students", ChipTone.Primary)
                    StatusChip("${d.homework.size} homework")
                    StatusChip("${d.missions.size} missions")
                }
                Spacer(Modifier.height(14.dp))
                PrimaryButton("Take attendance", { onTakeAttendance(classId) }, icon = Icons.Outlined.EventAvailable)
            }
        }

        item {
            ScrollableTabRow(
                selectedTabIndex = tab.ordinal,
                containerColor = EduColors.Background,
                contentColor = EduColors.PrimaryDark,
                edgePadding = 0.dp,
                divider = {},
                indicator = { positions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(positions[tab.ordinal]),
                        color = EduColors.Primary,
                    )
                },
            ) {
                ClassTab.entries.forEach { t ->
                    Tab(
                        selected = t == tab,
                        onClick = { tab = t },
                        text = {
                            Text(
                                t.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (t == tab) EduColors.PrimaryDark else EduColors.TextMuted,
                            )
                        },
                    )
                }
            }
        }

        when (tab) {
            ClassTab.Students -> {
                item { SecondaryButton("Enroll student", { creating = CreateKind.Enroll }, Modifier.fillMaxWidth(), Icons.Outlined.PersonAdd) }
                if (d.enrollments.isEmpty()) {
                    item { EduCard { EmptyState(Icons.Outlined.Groups, "No students yet", "Enroll students to track attendance and grades.") } }
                }
                items(d.enrollments, key = { "e${it.id}" }) { e ->
                    EduCard(onClick = { onOpenStudent(e.id!!) }, padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        ListItemRow(
                            title = e.student?.fullName ?: "Student",
                            subtitle = e.student?.email,
                            leading = { Avatar(e.student?.initials ?: "") },
                        ) {
                            IconButton(onClick = { removing = e }) {
                                Icon(Icons.Outlined.Delete, "Remove from class", tint = EduColors.TextLight)
                            }
                        }
                    }
                }
            }

            ClassTab.Lessons -> {
                item { SecondaryButton("New lesson", { creating = CreateKind.Lesson }, Modifier.fillMaxWidth(), Icons.Outlined.Add) }
                if (d.lessons.isEmpty()) {
                    item { EduCard { EmptyState(Icons.AutoMirrored.Outlined.MenuBook, "No lessons yet", "Published lessons appear for students right away.") } }
                }
                items(d.lessons, key = { "l${it.id}" }) { l ->
                    EduCard(onClick = { openLessonId = l.id }) {
                        ListItemRow(
                            title = l.title ?: "Lesson",
                            subtitle = l.learningObjective ?: l.description,
                            icon = Icons.AutoMirrored.Outlined.MenuBook,
                        ) { StatusChip(Dates.formatDate(l.publishedAt)) }
                    }
                }
            }

            ClassTab.Homework -> {
                item { SecondaryButton("Assign homework", { creating = CreateKind.Homework }, Modifier.fillMaxWidth(), Icons.Outlined.Add) }
                if (d.homework.isEmpty()) {
                    item { EduCard { EmptyState(Icons.AutoMirrored.Outlined.Assignment, "No homework yet", "Students and parents are notified when you assign work.") } }
                }
                items(d.homework, key = { "h${it.id}" }) { h ->
                    EduCard(onClick = { onOpenHomework(h.id!!) }) {
                        ListItemRow(
                            title = h.title ?: "Homework",
                            subtitle = "Tap to review submissions",
                            icon = Icons.AutoMirrored.Outlined.Assignment,
                        ) {
                            StatusChip(
                                Dates.dueLabel(h.dueDate),
                                if (Dates.isPast(h.dueDate)) ChipTone.Neutral else ChipTone.Primary,
                            )
                        }
                    }
                }
            }

            ClassTab.Missions -> {
                item { SecondaryButton("New family mission", { creating = CreateKind.Mission }, Modifier.fillMaxWidth(), Icons.Outlined.Add) }
                if (d.missions.isEmpty()) {
                    item { EduCard { EmptyState(Icons.Outlined.FavoriteBorder, "No missions yet", "Missions are activities families complete together at home.") } }
                }
                items(d.missions, key = { "m${it.id}" }) { m ->
                    EduCard(onClick = { onOpenMission(m.id!!) }) {
                        ListItemRow(
                            title = m.title ?: "Mission",
                            subtitle = Dates.dueLabel(m.dueDate),
                            icon = Icons.Outlined.FavoriteBorder,
                        ) { StatusChip("${m.points ?: 0} pts", ChipTone.Success) }
                    }
                }
            }

            ClassTab.Posts -> {
                item { SecondaryButton("Post announcement", { creating = CreateKind.Announcement }, Modifier.fillMaxWidth(), Icons.Outlined.Add) }
                if (d.announcements.isEmpty()) {
                    item { EduCard { EmptyState(Icons.Outlined.Campaign, "No announcements", "Share updates with students and parents.") } }
                }
                items(d.announcements, key = { "a${it.id}" }) { a ->
                    EduCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(a.title ?: "Announcement", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                            if (a.priority.equals("HIGH", true)) StatusChip("Important", ChipTone.Warning)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(a.message ?: "", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(Dates.formatDateTime(a.createdAt), style = MaterialTheme.typography.bodySmall, color = EduColors.TextLight)
                    }
                }
            }
        }
    }

    val detail = (loader.state as? Load.Ready)?.data
    val done: () -> Unit = { creating = null; loader.reload() }

    when (creating) {
        CreateKind.Enroll -> EnrollDialog(
            enrolledIds = detail?.enrollments?.mapNotNull { it.student?.id }?.toSet() ?: emptySet(),
            onDismiss = { creating = null },
            onEnroll = { studentId ->
                actions.run("Student enrolled", onSuccess = done) { repo.enroll(classId, studentId) }
            },
        )

        CreateKind.Lesson -> {
            var title by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var objective by rememberSaveable { mutableStateOf("") }
            var content by rememberSaveable { mutableStateOf("") }
            FormDialog("New lesson", "Publish", { creating = null }, {
                actions.run("Lesson published", onSuccess = done) {
                    repo.createLesson(classId, title.trim(), description.trim(), objective.trim(), content.trim())
                }
            }, confirmEnabled = title.isNotBlank(), busy = actions.busy) {
                EduTextField("Title", title, { title = it })
                EduTextField("Short description", description, { description = it })
                EduTextField("Learning objective", objective, { objective = it })
                EduTextField("Lesson content", content, { content = it }, singleLine = false, minLines = 4)
            }
        }

        CreateKind.Homework -> {
            var title by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var due by remember { mutableStateOf<LocalDate?>(LocalDate.now().plusDays(7)) }
            FormDialog("Assign homework", "Assign", { creating = null }, {
                actions.run("Homework assigned", onSuccess = done) {
                    repo.createHomework(classId, title.trim(), description.trim(), due?.let(Dates::endOfDayIso))
                }
            }, confirmEnabled = title.isNotBlank(), busy = actions.busy) {
                EduTextField("Title", title, { title = it })
                EduTextField("Instructions", description, { description = it }, singleLine = false, minLines = 3)
                DateField("Due date", due) { due = it }
            }
        }

        CreateKind.Mission -> {
            var title by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var objective by rememberSaveable { mutableStateOf("") }
            var instructions by rememberSaveable { mutableStateOf("") }
            var points by rememberSaveable { mutableStateOf("10") }
            var due by remember { mutableStateOf<LocalDate?>(LocalDate.now().plusDays(7)) }
            FormDialog("New family mission", "Create", { creating = null }, {
                actions.run("Family mission created", onSuccess = done) {
                    repo.createMission(
                        classId, title.trim(), description.trim(), objective.trim(), instructions.trim(),
                        points.toIntOrNull() ?: 0, due?.let(Dates::endOfDayIso),
                    )
                }
            }, confirmEnabled = title.isNotBlank(), busy = actions.busy) {
                EduTextField("Title", title, { title = it }, placeholder = "e.g. Measure your kitchen")
                EduTextField("Description", description, { description = it }, singleLine = false, minLines = 2)
                EduTextField("Learning objective", objective, { objective = it })
                EduTextField("Instructions for families", instructions, { instructions = it }, singleLine = false, minLines = 3)
                EduTextField("Points", points, { points = it.filter(Char::isDigit) }, keyboardType = KeyboardType.Number)
                DateField("Due date", due) { due = it }
            }
        }

        CreateKind.Announcement -> {
            var title by rememberSaveable { mutableStateOf("") }
            var message by rememberSaveable { mutableStateOf("") }
            var important by rememberSaveable { mutableStateOf(false) }
            FormDialog("Post announcement", "Post", { creating = null }, {
                actions.run("Announcement posted", onSuccess = done) {
                    repo.createAnnouncement(classId, title.trim(), message.trim(), important)
                }
            }, confirmEnabled = title.isNotBlank() && message.isNotBlank(), busy = actions.busy) {
                EduTextField("Title", title, { title = it })
                EduTextField("Message", message, { message = it }, singleLine = false, minLines = 4)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Mark as important", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Switch(
                        checked = important,
                        onCheckedChange = { important = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = EduColors.Primary),
                    )
                }
            }
        }

        null -> Unit
    }

    openLessonId?.let { lessonId ->
        val lesson = detail?.lessons?.firstOrNull { it.id == lessonId }
        if (lesson != null) LessonMaterialsDialog(lesson) { openLessonId = null }
    }

    removing?.let { e ->
        ConfirmDialog(
            title = "Remove student?",
            message = "${e.student?.fullName ?: "This student"} will be removed from the class roster.",
            confirmText = "Remove",
            onDismiss = { removing = null },
            onConfirm = {
                removing = null
                actions.run("Student removed", onSuccess = loader.reload) { repo.unenroll(e.id!!) }
            },
        )
    }
}

@Composable
private fun EnrollDialog(enrolledIds: Set<Long>, onDismiss: () -> Unit, onEnroll: (Long) -> Unit) {
    val repo = LocalAppContainer.current.repository
    val students = rememberLoader(Unit) { repo.allStudents() }
    var query by rememberSaveable { mutableStateOf("") }

    FormDialog("Enroll student", "Done", onDismiss, onDismiss) {
        EduTextField("Search", query, { query = it }, placeholder = "Name or email")
        when (val s = students.state) {
            is Load.Loading -> Text("Loading students...", style = MaterialTheme.typography.bodySmall)
            is Load.Error -> Text(s.message, color = EduColors.Danger, style = MaterialTheme.typography.bodySmall)
            is Load.Ready -> {
                val available = s.data
                    .filter { it.id !in enrolledIds }
                    .filter {
                        query.isBlank() || it.fullName.contains(query, true) || (it.email ?: "").contains(query, true)
                    }
                if (available.isEmpty()) {
                    Text(
                        "No matching student accounts. Students appear here after they sign up in the app.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                available.take(30).forEach { student ->
                    ListItemRow(
                        title = student.fullName,
                        subtitle = student.email,
                        leading = { Avatar(student.initials, 32.dp) },
                        onClick = { onEnroll(student.id!!) },
                    ) { Icon(Icons.Outlined.PersonAdd, null, tint = EduColors.Primary) }
                }
            }
        }
    }
}

@Composable
private fun LessonMaterialsDialog(lesson: Lesson, onDismiss: () -> Unit) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val materials = rememberLoader(lesson.id) { repo.materials(lesson.id!!) }
    var adding by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }

    FormDialog(
        title = lesson.title ?: "Lesson",
        confirmText = if (adding) "Add material" else "Close",
        busy = actions.busy,
        confirmEnabled = !adding || name.isNotBlank(),
        onDismiss = onDismiss,
        onConfirm = {
            if (!adding) {
                onDismiss()
            } else {
                actions.run("Material added", onSuccess = {
                    adding = false; name = ""; type = ""; url = ""; materials.reload()
                }) { repo.addMaterial(lesson.id!!, name.trim(), type.trim(), url.trim()) }
            }
        },
    ) {
        lesson.learningObjective?.let {
            Text("OBJECTIVE", style = MaterialTheme.typography.labelSmall)
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }
        lesson.content?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }

        Text("LEARNING MATERIALS", style = MaterialTheme.typography.labelSmall)
        val list = materials.data.orEmpty()
        if (materials.state is Load.Ready && list.isEmpty()) {
            Text("No materials yet.", style = MaterialTheme.typography.bodySmall)
        }
        list.forEach { m ->
            ListItemRow(
                title = m.materialName ?: "Material",
                subtitle = listOfNotNull(m.materialType, m.fileUrl).joinToString(" · "),
                icon = Icons.Outlined.Folder,
            )
        }

        if (adding) {
            EduTextField("Name", name, { name = it })
            EduTextField("Type", type, { type = it }, placeholder = "PDF, Video, Link...")
            EduTextField("Link", url, { url = it }, keyboardType = KeyboardType.Uri, placeholder = "https://")
        } else {
            SecondaryButton("Add material", { adding = true }, Modifier.fillMaxWidth(), Icons.Outlined.Add)
        }
    }
}
