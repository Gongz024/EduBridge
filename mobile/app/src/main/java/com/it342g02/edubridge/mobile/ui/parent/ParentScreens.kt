package com.it342g02.edubridge.mobile.ui.parent

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.FamilyLearningMission
import com.it342g02.edubridge.mobile.data.model.ParentStudent
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.data.repository.MissionWithProgress
import com.it342g02.edubridge.mobile.data.repository.StudentOverview
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.ErrorPanel
import com.it342g02.edubridge.mobile.ui.components.FormDialog
import com.it342g02.edubridge.mobile.ui.components.LinkButton
import com.it342g02.edubridge.mobile.ui.components.ListItemRow
import com.it342g02.edubridge.mobile.ui.components.Load
import com.it342g02.edubridge.mobile.ui.components.LoadedList
import com.it342g02.edubridge.mobile.ui.components.Loader
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PageHeading
import com.it342g02.edubridge.mobile.ui.components.PrimaryButton
import com.it342g02.edubridge.mobile.ui.components.SectionHeader
import com.it342g02.edubridge.mobile.ui.components.StatGrid
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.components.rememberActionRunner
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.navigation.Routes
import com.it342g02.edubridge.mobile.ui.student.AnnouncementsCard
import com.it342g02.edubridge.mobile.ui.student.progressSections
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.util.Dates

/**
 * State shared by all parent tabs: the linked children, which child is selected,
 * and that child's overview and missions. Lives above the NavHost so switching tabs
 * keeps the selection and cached data.
 */
@Stable
class ParentState(
    val user: User,
    val children: Loader<List<ParentStudent>>,
    val selected: ParentStudent?,
    val select: (Long) -> Unit,
    val overview: Loader<StudentOverview?>,
    val missions: Loader<List<MissionWithProgress>>,
) {
    fun reloadAll() {
        children.reload()
        overview.reload()
        missions.reload()
    }
}

@Composable
fun rememberParentState(user: User): ParentState {
    val repo = LocalAppContainer.current.repository
    val children = rememberLoader(user.id) { repo.children(user.id!!) }
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }

    val links = children.data.orEmpty()
    val selected = links.firstOrNull { it.id == selectedId } ?: links.firstOrNull()

    val overview = rememberLoader(selected?.student?.id) { selected?.student?.let { repo.studentOverview(it) } }
    val missions = rememberLoader(selected?.id) { selected?.let { repo.missionsForChild(it) } ?: emptyList() }

    return ParentState(user, children, selected, { selectedId = it }, overview, missions)
}

// ---------------------------------------------------------------------------
// Child selector + linking
// ---------------------------------------------------------------------------

@Composable
private fun ChildSelector(state: ParentState, onAddChild: () -> Unit) {
    val links = state.children.data.orEmpty()
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        links.forEach { link ->
            FilterChip(
                selected = link.id == state.selected?.id,
                onClick = { state.select(link.id!!) },
                label = { Text(link.student?.firstName ?: "Child", style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EduColors.PrimaryLight,
                    selectedLabelColor = EduColors.PrimaryDark,
                    containerColor = EduColors.Surface,
                ),
            )
        }
        FilterChip(
            selected = false,
            onClick = onAddChild,
            label = { Text("Add child", style = MaterialTheme.typography.labelLarge) },
            leadingIcon = { Icon(Icons.Outlined.Add, null, Modifier.padding(0.dp)) },
            colors = FilterChipDefaults.filterChipColors(containerColor = EduColors.Surface),
        )
    }
}

@Composable
private fun LinkChildDialog(state: ParentState, onDismiss: () -> Unit) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    var email by rememberSaveable { mutableStateOf("") }
    var relationship by rememberSaveable { mutableStateOf("Parent") }

    FormDialog(
        title = "Link your child",
        confirmText = "Link",
        busy = actions.busy,
        confirmEnabled = email.contains("@"),
        onDismiss = onDismiss,
        onConfirm = {
            actions.run("Child linked", onSuccess = { onDismiss(); state.reloadAll() }) {
                val link = repo.linkChild(state.user, email, relationship.trim())
                link.id?.let(state.select)
            }
        },
    ) {
        Text(
            "Enter the email your child uses for their EduBridge student account.",
            style = MaterialTheme.typography.bodySmall,
        )
        EduTextField("Child's email", email, { email = it }, keyboardType = KeyboardType.Email)
        EduTextField("Relationship", relationship, { relationship = it }, placeholder = "Mother, Father, Guardian...")
    }
}

/** Header shared by all parent tabs: page heading, child selector and the link dialog. */
private fun LazyListScope.parentHeader(
    state: ParentState,
    eyebrow: String,
    title: String,
    description: String,
    onAddChild: () -> Unit,
) {
    item {
        PageHeading(eyebrow, title, description)
        Spacer(Modifier.height(10.dp))
        ChildSelector(state, onAddChild)
    }
    if (state.children.data.isNullOrEmpty()) {
        item {
            EduCard {
                EmptyState(
                    Icons.Outlined.FamilyRestroom,
                    "Link your child's account",
                    "Add your child using the email from their student account to follow their classes.",
                ) { PrimaryButton("Add child", onAddChild, icon = Icons.Outlined.Add) }
            }
        }
    }
}

/** Renders a child-dependent section, with inline loading/error states. */
@Composable
private fun <T> ChildSection(loader: Loader<T>, content: @Composable (T) -> Unit) {
    when (val s = loader.state) {
        is Load.Loading -> Box(
            Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator(color = EduColors.Primary) }
        is Load.Error -> Box(Modifier.height(220.dp)) { ErrorPanel(s.message, loader.reload) }
        is Load.Ready -> content(s.data)
    }
}

// ---------------------------------------------------------------------------
// Tabs
// ---------------------------------------------------------------------------

@Composable
fun ParentHomeScreen(user: User, state: ParentState, onOpen: (String) -> Unit) {
    var adding by rememberSaveable { mutableStateOf(false) }

    LoadedList(state.children) { links ->
        parentHeader(
            state,
            Dates.todayEyebrow(),
            "${Dates.greeting()}, ${user.firstName}!",
            "Stay connected with your child's learning.",
        ) { adding = true }

        if (links.isNotEmpty()) {
            item {
                ChildSection(state.overview) { o ->
                    if (o != null) ChildSnapshot(o, state, onOpen)
                }
            }
        }
    }

    if (adding) LinkChildDialog(state) { adding = false }
}

@Composable
private fun ChildSnapshot(o: StudentOverview, state: ParentState, onOpen: (String) -> Unit) {
    val name = o.student.firstName ?: "Your child"
    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StatGrid(
            listOf(
                Triple("Average grade", o.averageGrade?.let { "${it.toInt()}%" } ?: "—", Icons.Outlined.Grade),
                Triple("Attendance", o.attendanceRate?.let { "${it.toInt()}%" } ?: "—", Icons.Outlined.EventAvailable),
                Triple("Homework due", o.pendingHomework.size.toString(), Icons.AutoMirrored.Outlined.Assignment),
                Triple("Points", o.totalPoints.toString(), Icons.Outlined.Stars),
            ),
        )

        val todo = state.missions.data.orEmpty().count { it.participation == null }
        EduCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) {
                    SectionHeader(
                        "Family missions",
                        if (todo == 0) "No missions waiting" else "$todo to do together with $name",
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            PrimaryButton("Open missions", { onOpen(Routes.P_MISSIONS) }, icon = Icons.Outlined.FavoriteBorder)
        }

        EduCard {
            SectionHeader("$name's homework", "What's due in class") {
                LinkButton("Progress") { onOpen(Routes.P_PROGRESS) }
            }
            if (o.homework.isEmpty()) Text("No homework assigned yet.", style = MaterialTheme.typography.bodySmall)
            o.homework.take(5).forEach { hw ->
                val submitted = o.submissionFor(hw) != null
                ListItemRow(
                    title = hw.title ?: "Homework",
                    subtitle = "${hw.schoolClass?.className ?: ""} · ${Dates.dueLabel(hw.dueDate)}",
                    icon = Icons.AutoMirrored.Outlined.Assignment,
                ) {
                    StatusChip(
                        if (submitted) "Submitted" else "Not submitted",
                        if (submitted) ChipTone.Success else if (Dates.isPast(hw.dueDate)) ChipTone.Danger else ChipTone.Warning,
                    )
                }
            }
        }

        AnnouncementsCard(o)
    }
}

@Composable
fun ParentMissionsScreen(state: ParentState) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    var adding by rememberSaveable { mutableStateOf(false) }
    var confirming by remember { mutableStateOf<FamilyLearningMission?>(null) }

    LoadedList(state.children) { links ->
        parentHeader(
            state,
            "ENGAGEMENT",
            "Family Missions",
            "Short learning activities to complete together at home.",
        ) { adding = true }

        if (links.isNotEmpty()) {
            item {
                ChildSection(state.missions) { missions ->
                    androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (missions.isEmpty()) {
                            EduCard {
                                EmptyState(Icons.Outlined.FavoriteBorder, "No missions yet", "Missions from teachers will show up here.")
                            }
                        }
                        missions.forEach { entry -> MissionCard(entry) { confirming = entry.mission } }
                    }
                }
            }
        }
    }

    if (adding) LinkChildDialog(state) { adding = false }

    confirming?.let { mission ->
        val link = state.selected
        FormDialog(
            title = "Mission complete?",
            confirmText = "Yes, we did it",
            busy = actions.busy,
            onDismiss = { confirming = null },
            onConfirm = {
                if (link != null) {
                    actions.run("Great work! Your teacher has been notified.", onSuccess = {
                        confirming = null
                        state.missions.reload()
                    }) { repo.completeMission(link, mission) }
                }
            },
        ) {
            Text(
                "Confirm that you and ${link?.student?.firstName ?: "your child"} completed \"${mission.title}\" together. " +
                    "Your child's teacher will review it and award the points.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun MissionCard(item: MissionWithProgress, onComplete: () -> Unit) {
    val m = item.mission
    val p = item.participation
    EduCard {
        Text(m.schoolClass?.className?.uppercase() ?: "", style = MaterialTheme.typography.labelSmall)
        Text(m.title ?: "Mission", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("${m.points ?: 0} pts", ChipTone.Success)
            if (p == null) StatusChip(Dates.dueLabel(m.dueDate), if (Dates.isPast(m.dueDate)) ChipTone.Danger else ChipTone.Primary)
            else StatusChip("Completed ${Dates.formatDate(p.completedAt)}", ChipTone.Success)
        }
        m.description?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.bodyMedium) }
        m.learningObjective?.let {
            Spacer(Modifier.height(8.dp))
            Text("WHAT YOUR CHILD LEARNS", style = MaterialTheme.typography.labelSmall)
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }
        m.instructions?.let {
            Spacer(Modifier.height(8.dp))
            Text("HOW TO DO IT", style = MaterialTheme.typography.labelSmall)
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }
        p?.teacherFeedback?.let {
            Spacer(Modifier.height(8.dp))
            Text("TEACHER FEEDBACK", style = MaterialTheme.typography.labelSmall)
            Text(it, style = MaterialTheme.typography.bodyMedium)
        }
        if (p == null) {
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Mark as completed together", onComplete, icon = Icons.Outlined.CheckCircle)
        }
    }
}

@Composable
fun ParentProgressScreen(state: ParentState) {
    var adding by rememberSaveable { mutableStateOf(false) }

    LoadedList(state.children) { links ->
        parentHeader(
            state,
            "ACADEMIC",
            "${state.selected?.student?.firstName ?: "Child"}'s Progress",
            "Grades, attendance and rewards across all classes.",
        ) { adding = true }

        if (links.isNotEmpty()) {
            when (val s = state.overview.state) {
                is Load.Ready -> s.data?.let { progressSections(it) }
                else -> item { ChildSection(state.overview) {} }
            }
        }
    }

    if (adding) LinkChildDialog(state) { adding = false }
}
