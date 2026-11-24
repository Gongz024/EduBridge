package com.it342g02.edubridge.mobile.ui.teacher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.FormDialog
import com.it342g02.edubridge.mobile.ui.components.IconTile
import com.it342g02.edubridge.mobile.ui.components.LinkButton
import com.it342g02.edubridge.mobile.ui.components.ListItemRow
import com.it342g02.edubridge.mobile.ui.components.LoadedList
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PageHeading
import com.it342g02.edubridge.mobile.ui.components.PrimaryButton
import com.it342g02.edubridge.mobile.ui.components.SectionHeader
import com.it342g02.edubridge.mobile.ui.components.StatGrid
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.components.rememberActionRunner
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.util.Dates

/** Mobile version of the web Dashboard: same greeting, same five stat cards, plus the web's quick actions. */
@Composable
fun TeacherHomeScreen(user: User, onOpenClasses: () -> Unit, onOpenClass: (Long) -> Unit) {
    val repo = LocalAppContainer.current.repository
    val loader = rememberLoader(user.id) { repo.teacherDashboard(user.id!!) }

    LoadedList(loader) { d ->
        item {
            PageHeading(
                eyebrow = Dates.todayEyebrow(),
                title = "${Dates.greeting()}, ${user.firstName}!",
                description = "Here's an overview of your classes and learning activities.",
            )
        }

        item {
            StatGrid(
                listOf(
                    Triple("My Classes", d.classes.size.toString(), Icons.Outlined.School),
                    Triple("Students", d.studentCount.toString(), Icons.Outlined.Groups),
                    Triple("Homework", d.homework.size.toString(), Icons.AutoMirrored.Outlined.Assignment),
                    Triple("Family Missions", d.missions.size.toString(), Icons.Outlined.FavoriteBorder),
                    Triple("Announcements", d.announcements.size.toString(), Icons.Outlined.Campaign),
                ),
            )
        }

        // Quick actions all start from a class; with a single class, jump straight into it.
        val openTarget: () -> Unit = {
            val only = d.classes.singleOrNull()?.id
            if (only != null) onOpenClass(only) else onOpenClasses()
        }

        item {
            EduCard {
                SectionHeader("Quick Actions", "Common teaching tasks")
                Spacer(Modifier.height(8.dp))
                QuickAction(Icons.Outlined.EventAvailable, "Take Attendance", openTarget)
                QuickAction(Icons.AutoMirrored.Outlined.MenuBook, "Create Lesson", openTarget)
                QuickAction(Icons.AutoMirrored.Outlined.Assignment, "Create Homework", openTarget)
                QuickAction(Icons.Outlined.FavoriteBorder, "Create Family Mission", openTarget)
            }
        }

        // .family-missions-card banner
        item {
            EduCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(Icons.Outlined.FavoriteBorder, 44.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("FAMILY LEARNING MISSIONS", style = MaterialTheme.typography.labelSmall)
                        Text("Make learning a family activity", style = MaterialTheme.typography.titleMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(d.missions.size.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EduColors.Primary)
                        Text("Missions", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Create short educational activities that students and parents can complete together at home.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton("Create Family Mission", openTarget, icon = Icons.AutoMirrored.Outlined.ArrowForward)
            }
        }

        item {
            EduCard {
                SectionHeader("Upcoming Homework", "What's due across your classes") {
                    LinkButton("View classes", onOpenClasses)
                }
                val upcoming = d.homework.filter { !Dates.isPast(it.dueDate) }.take(4)
                if (upcoming.isEmpty()) {
                    EmptyState(Icons.AutoMirrored.Outlined.Assignment, "Nothing due", "Homework you assign will appear here.")
                }
                upcoming.forEach { hw ->
                    ListItemRow(
                        title = hw.title ?: "Homework",
                        subtitle = hw.schoolClass?.className,
                        icon = Icons.AutoMirrored.Outlined.Assignment,
                        onClick = { hw.schoolClass?.id?.let(onOpenClass) },
                    ) { StatusChip(Dates.dueLabel(hw.dueDate), ChipTone.Primary) }
                }
            }
        }

        if (d.announcements.isNotEmpty()) {
            item {
                EduCard {
                    SectionHeader("Recent Announcements")
                    d.announcements.take(3).forEach { a ->
                        ListItemRow(
                            title = a.title ?: "Announcement",
                            subtitle = "${a.schoolClass?.className ?: ""} · ${Dates.formatDateTime(a.createdAt)}",
                            icon = Icons.Outlined.Campaign,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    ListItemRow(title = label, icon = icon, onClick = onClick) {
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = EduColors.TextLight)
    }
}

@Composable
fun ClassesScreen(user: User, onOpenClass: (Long) -> Unit) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val loader = rememberLoader(user.id) { repo.teacherClasses(user.id!!) }
    var creating by rememberSaveable { mutableStateOf(false) }

    LoadedList(loader) { classes ->
        item {
            PageHeading("TEACHING", "My Classes", "Manage your classes, rosters and class activities.")
            Spacer(Modifier.height(12.dp))
            PrimaryButton("New class", { creating = true }, icon = Icons.Outlined.Add)
        }

        if (classes.isEmpty()) {
            item {
                EduCard {
                    EmptyState(Icons.Outlined.School, "No classes yet", "Create your first class to start adding students.")
                }
            }
        }

        items(classes, key = { it.id!! }) { c ->
            EduCard(onClick = { onOpenClass(c.id!!) }) {
                ListItemRow(
                    title = c.className ?: "Class",
                    subtitle = listOfNotNull(c.gradeLevel, c.schoolYear).joinToString(" · ").ifBlank { null },
                    icon = Icons.Outlined.School,
                ) { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = EduColors.TextLight) }
            }
        }
    }

    if (creating) {
        var name by rememberSaveable { mutableStateOf("") }
        var grade by rememberSaveable { mutableStateOf("") }
        var year by rememberSaveable { mutableStateOf("") }
        FormDialog(
            title = "New class",
            confirmText = "Create",
            busy = actions.busy,
            confirmEnabled = name.isNotBlank(),
            onDismiss = { creating = false },
            onConfirm = {
                actions.run("Class created", onSuccess = { creating = false; loader.reload() }) {
                    repo.createClass(user, name.trim(), grade.trim(), year.trim())
                }
            },
        ) {
            EduTextField("Class name", name, { name = it }, placeholder = "e.g. IT 4A — Mathematics")
            EduTextField("Grade level", grade, { grade = it }, placeholder = "e.g. Grade 4")
            EduTextField("School year", year, { year = it }, placeholder = "e.g. 2026–2027")
        }
    }
}
