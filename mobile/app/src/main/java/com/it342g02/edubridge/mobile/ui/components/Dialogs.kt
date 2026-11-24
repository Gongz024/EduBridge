package com.it342g02.edubridge.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.ui.theme.EduRadius
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/** A dialog with a scrollable form body and Cancel / confirm actions. */
@Composable
fun FormDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    busy: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        containerColor = EduColors.Surface,
        shape = RoundedCornerShape(EduRadius.Lg),
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = confirmEnabled && !busy) {
                Text(if (busy) "Saving..." else confirmText, color = EduColors.Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !busy) {
                Text("Cancel", color = EduColors.TextMuted)
            }
        },
    )
}

@Composable
fun ConfirmDialog(title: String, message: String, confirmText: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EduColors.Surface,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText, color = EduColors.Danger) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = EduColors.TextMuted) } },
    )
}

private val displayFormat = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.US)

/** Tappable field that opens a Material date picker. Value is null until picked. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(label: String, value: LocalDate?, onChange: (LocalDate?) -> Unit) {
    var open by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(EduRadius.Sm))
                .clickable { open = true }
                .padding(vertical = 2.dp)
                .then(Modifier),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.CalendarMonth, null, tint = EduColors.Primary)
            Spacer(Modifier.width(10.dp))
            Text(
                value?.format(displayFormat) ?: "Pick a date",
                style = MaterialTheme.typography.bodyLarge,
                color = if (value == null) EduColors.TextLight else EduColors.Text,
                modifier = Modifier.weight(1f),
            )
            if (value != null) {
                TextButton(onClick = { onChange(null) }) { Text("Clear", color = EduColors.TextMuted) }
            }
        }
    }

    if (open) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (value ?: LocalDate.now())
                .atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    open = false
                }) { Text("OK", color = EduColors.Primary) }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = state)
        }
    }
}
