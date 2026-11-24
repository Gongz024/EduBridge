package com.it342g02.edubridge.mobile.ui.student

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.AttendanceStatus
import com.it342g02.edubridge.mobile.data.repository.StudentOverview
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.ListItemRow
import com.it342g02.edubridge.mobile.ui.components.ProgressRow
import com.it342g02.edubridge.mobile.ui.components.SectionHeader
import com.it342g02.edubridge.mobile.ui.components.StatGrid
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.shared.AttendanceItem
import com.it342g02.edubridge.mobile.ui.shared.GradeItem
import com.it342g02.edubridge.mobile.ui.shared.gradeTone
import com.it342g02.edubridge.mobile.util.Dates

/**
 * Progress sections used by both the student "Progress" tab and the parent's view of a
 * child, so both see exactly the same breakdown.
 */
fun LazyListScope.progressSections(o: StudentOverview) {
    item {
        StatGrid(
            listOf(
                Triple("Average grade", o.averageGrade?.let { "${it.toInt()}%" } ?: "—", Icons.Outlined.Grade),
                Triple("Attendance", o.attendanceRate?.let { "${it.toInt()}%" } ?: "—", Icons.Outlined.EventAvailable),
                Triple("Points earned", o.totalPoints.toString(), Icons.Outlined.Stars),
                Triple("Badges", o.badges.size.toString(), Icons.Outlined.EmojiEvents),
            ),
        )
    }

    item {
        EduCard {
            SectionHeader("By class", "Grades and attendance in each class")
            if (o.records.isEmpty()) {
                EmptyState(Icons.Outlined.School, "Not enrolled in any class yet", "Your teacher will add you to a class.")
            }
            o.records.forEach { r ->
                val avg = r.grades.mapNotNull { it.percent }.takeIf { it.isNotEmpty() }?.average()
                val rate = r.attendance.takeIf { it.isNotEmpty() }
                    ?.let { list -> list.count { it.attendanceStatus != AttendanceStatus.ABSENT } * 100.0 / list.size }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        r.enrollment.schoolClass?.className ?: "Class",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                    )
                    StatusChip(avg?.let { "${it.toInt()}%" } ?: "No grades", gradeTone(avg))
                }
                ProgressRow("Grade average", avg)
                ProgressRow("Attendance", rate)
            }
        }
    }

    item {
        EduCard {
            SectionHeader("Recent grades")
            val recent = o.grades.sortedByDescending { it.gradeDate ?: "" }.take(8)
            if (recent.isEmpty()) Text("No grades yet.", style = MaterialTheme.typography.bodySmall)
            recent.forEach { GradeItem(it, showClass = true) }
        }
    }

    item {
        EduCard {
            SectionHeader("Recent attendance")
            val recent = o.attendance.sortedByDescending { it.attendanceDate ?: "" }.take(8)
            if (recent.isEmpty()) Text("No attendance recorded yet.", style = MaterialTheme.typography.bodySmall)
            recent.forEach { AttendanceItem(it, showClass = true) }
        }
    }

    if (o.points.isNotEmpty() || o.badges.isNotEmpty()) {
        item {
            EduCard {
                SectionHeader("Rewards", "${o.totalPoints} points total")
                o.badges.forEach { b ->
                    ListItemRow(
                        title = b.badge?.badgeName ?: "Badge",
                        subtitle = b.badge?.description,
                        icon = Icons.Outlined.EmojiEvents,
                    ) { StatusChip(Dates.formatDate(b.awardedAt)) }
                }
                o.points.take(8).forEach { p ->
                    ListItemRow(
                        title = p.reason ?: "Points",
                        subtitle = Dates.formatDateTime(p.earnedAt),
                        icon = Icons.Outlined.Stars,
                    ) { StatusChip("+${p.points ?: 0}", ChipTone.Success) }
                }
            }
        }
    }
}

/** Latest class announcements card. */
@Composable
fun AnnouncementsCard(o: StudentOverview, limit: Int = 3) {
    EduCard {
        SectionHeader("Announcements", "From your teachers")
        val list = o.announcements.take(limit)
        if (list.isEmpty()) {
            EmptyState(Icons.Outlined.Campaign, "No announcements yet")
        }
        list.forEach { a ->
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(a.title ?: "Announcement", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                if (a.priority.equals("HIGH", true)) StatusChip("Important", ChipTone.Warning)
            }
            Text(a.message ?: "", style = MaterialTheme.typography.bodyMedium)
            Text(
                "${a.schoolClass?.className ?: ""} · ${Dates.formatDateTime(a.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
