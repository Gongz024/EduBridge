package com.it342g02.edubridge.mobile.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.Attendance
import com.it342g02.edubridge.mobile.data.model.AttendanceStatus
import com.it342g02.edubridge.mobile.data.model.Grade
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.util.Dates

/** Grade and attendance rows shared by the teacher, parent and student views. */

fun AttendanceStatus?.tone(): ChipTone = when (this) {
    AttendanceStatus.PRESENT -> ChipTone.Success
    AttendanceStatus.LATE -> ChipTone.Warning
    AttendanceStatus.ABSENT -> ChipTone.Danger
    AttendanceStatus.EXCUSED -> ChipTone.Neutral
    null -> ChipTone.Neutral
}

fun AttendanceStatus?.label(): String =
    this?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Not recorded"

fun gradeTone(percent: Double?): ChipTone = when {
    percent == null -> ChipTone.Neutral
    percent >= 85 -> ChipTone.Success
    percent >= 75 -> ChipTone.Primary
    percent >= 60 -> ChipTone.Warning
    else -> ChipTone.Danger
}

private fun Double.clean(): String = if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)

@Composable
fun GradeItem(grade: Grade, showClass: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(grade.assessmentName ?: "Assessment", style = MaterialTheme.typography.titleSmall)
            val meta = listOfNotNull(
                grade.studentClass?.schoolClass?.className?.takeIf { showClass },
                Dates.formatDate(grade.gradeDate),
                grade.remarks,
            ).joinToString(" · ")
            Text(meta, style = MaterialTheme.typography.bodySmall)
        }
        val score = if (grade.score != null && grade.maxScore != null) {
            "${grade.score.clean()}/${grade.maxScore.clean()}"
        } else {
            grade.score?.clean() ?: "—"
        }
        StatusChip(score, gradeTone(grade.percent))
    }
}

@Composable
fun AttendanceItem(record: Attendance, showClass: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(Dates.formatDate(record.attendanceDate), style = MaterialTheme.typography.titleSmall)
            val meta = listOfNotNull(
                record.studentClass?.schoolClass?.className?.takeIf { showClass },
                record.remarks,
            ).joinToString(" · ")
            if (meta.isNotBlank()) Text(meta, style = MaterialTheme.typography.bodySmall)
        }
        StatusChip(record.attendanceStatus.label(), record.attendanceStatus.tone())
    }
}
