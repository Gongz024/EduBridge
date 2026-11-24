package com.it342g02.edubridge.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.ui.theme.EduRadius

// ---------------------------------------------------------------------------
// Brand & identity (.brand-mark, .teacher-avatar)
// ---------------------------------------------------------------------------

@Composable
fun BrandMark(size: Dp = 34.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(9.dp))
            .background(EduColors.Primary),
        contentAlignment = Alignment.Center,
    ) {
        Text("E", color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.47f).sp)
    }
}

@Composable
fun Avatar(initials: String, size: Dp = 36.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(EduColors.PrimaryLight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials.ifBlank { "?" },
            color = EduColors.PrimaryDark,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.36f).sp,
        )
    }
}

// ---------------------------------------------------------------------------
// Page structure (.page-heading, .eyebrow, .page-description)
// ---------------------------------------------------------------------------

@Composable
fun PageHeading(eyebrow: String, title: String, description: String? = null) {
    Column(Modifier.padding(bottom = 4.dp)) {
        Text(eyebrow, style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(6.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (description != null) {
            Spacer(Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = EduColors.TextMuted)
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null, action: (@Composable () -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
        action?.invoke()
    }
}

/** The white bordered card used throughout the web app (.dashboard-card / .stat-card). */
@Composable
fun EduCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(EduRadius.Lg)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(EduColors.Surface)
            .border(1.dp, EduColors.Border, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding),
        content = content,
    )
}

/** .stat-card: muted label above a large number. */
@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    EduCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = EduColors.TextMuted,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (icon != null) Icon(icon, null, tint = EduColors.Primary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = EduColors.Text)
    }
}

/** Two stat cards per row, like the web .stat-grid collapsing on narrow screens. */
@Composable
fun StatGrid(items: List<Triple<String, String, ImageVector?>>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (label, value, icon) ->
                    StatCard(label, value, Modifier.weight(1f), icon)
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/** .progress-row from the web ProgressOverview card. */
@Composable
fun ProgressRow(label: String, percent: Double?) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Row {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text(percent?.let { "${it.toInt()}%" } ?: "—", style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(EduColors.PrimaryLight),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(((percent ?: 0.0) / 100).toFloat().coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(EduColors.Primary),
            )
        }
    }
}

enum class ChipTone { Neutral, Primary, Success, Warning, Danger }

@Composable
fun StatusChip(text: String, tone: ChipTone = ChipTone.Neutral) {
    val (bg, fg) = when (tone) {
        ChipTone.Neutral -> EduColors.Hover to EduColors.TextMuted
        ChipTone.Primary -> EduColors.PrimaryLight to EduColors.PrimaryDark
        ChipTone.Success -> EduColors.SuccessLight to EduColors.Success
        ChipTone.Warning -> EduColors.WarningLight to EduColors.Warning
        ChipTone.Danger -> EduColors.DangerLight to EduColors.Danger
    }
    Text(
        text,
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
    )
}

/** A tappable list row inside a card: leading icon tile, title/subtitle, trailing slot. */
@Composable
fun ListItemRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(EduRadius.Md))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            leading != null -> leading()
            icon != null -> IconTile(icon)
        }
        if (leading != null || icon != null) Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}

/** .quick-action-icon / .lesson-icon */
@Composable
fun IconTile(icon: ImageVector, size: Dp = 38.dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(EduRadius.Md - 2.dp))
            .background(EduColors.PrimaryLight),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, null, tint = EduColors.Primary, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, body: String? = null, action: (@Composable () -> Unit)? = null) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconTile(icon, 48.dp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, textAlign = TextAlign.Center)
        if (body != null) {
            Spacer(Modifier.height(4.dp))
            Text(body, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
        }
        if (action != null) {
            Spacer(Modifier.height(12.dp))
            action()
        }
    }
}

// ---------------------------------------------------------------------------
// Forms (.form-field, .primary-button, .form-error)
// ---------------------------------------------------------------------------

@Composable
fun EduTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    Column(modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = EduColors.Text)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let { { Text(it, color = EduColors.TextLight) } },
            singleLine = singleLine,
            minLines = minLines,
            textStyle = MaterialTheme.typography.bodyLarge,
            visualTransformation = if (isPassword) PasswordVisualTransformation()
            else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            shape = RoundedCornerShape(EduRadius.Sm),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = EduColors.InputBackground,
                focusedContainerColor = EduColors.Surface,
                unfocusedBorderColor = EduColors.Border,
                focusedBorderColor = EduColors.Primary,
                cursorColor = EduColors.Primary,
            ),
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(EduRadius.Sm),
        colors = ButtonDefaults.buttonColors(
            containerColor = EduColors.Primary,
            disabledContainerColor = EduColors.Primary.copy(alpha = 0.6f),
            disabledContentColor = Color.White,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            if (icon != null) {
                Icon(icon, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(EduRadius.Sm),
        border = BorderStroke(1.dp, EduColors.Border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = EduColors.PrimaryDark),
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** .text-button */
@Composable
fun LinkButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, color = EduColors.Primary, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun FormError(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(
            message,
            color = EduColors.Danger,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// Loading scaffolds
// ---------------------------------------------------------------------------

@Composable
fun CenteredLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = EduColors.Primary)
    }
}

@Composable
fun ErrorPanel(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.ErrorOutline, null, tint = EduColors.Danger, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(10.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(14.dp))
        SecondaryButton("Try again", onRetry)
    }
}

/**
 * Standard screen body: shows loading/error states, then a pull-to-refresh list
 * built by [content] once the data is ready.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> LoadedList(
    loader: Loader<T>,
    contentPadding: PaddingValues = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    content: LazyListScope.(T) -> Unit,
) {
    when (val state = loader.state) {
        is Load.Loading -> CenteredLoading()
        is Load.Error -> ErrorPanel(state.message, loader.reload)
        is Load.Ready -> PullToRefreshBox(
            isRefreshing = loader.isRefreshing,
            onRefresh = loader.reload,
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content(state.data)
            }
        }
    }
}
