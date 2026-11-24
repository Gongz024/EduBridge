package com.it342g02.edubridge.mobile.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.RateReview
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.BuildConfig
import com.it342g02.edubridge.mobile.data.model.Notification
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.ui.components.Avatar
import com.it342g02.edubridge.mobile.ui.components.ChipTone
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.EmptyState
import com.it342g02.edubridge.mobile.ui.components.IconTile
import com.it342g02.edubridge.mobile.ui.components.LinkButton
import com.it342g02.edubridge.mobile.ui.components.LoadedList
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PageHeading
import com.it342g02.edubridge.mobile.ui.components.PrimaryButton
import com.it342g02.edubridge.mobile.ui.components.SecondaryButton
import com.it342g02.edubridge.mobile.ui.components.StatusChip
import com.it342g02.edubridge.mobile.ui.components.rememberActionRunner
import com.it342g02.edubridge.mobile.ui.components.rememberLoader
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.util.Dates

private fun iconFor(n: Notification): ImageVector = when {
    n.homework != null || n.notificationType.equals("HOMEWORK", true) -> Icons.AutoMirrored.Outlined.Assignment
    n.mission != null || n.notificationType.equals("MISSION", true) -> Icons.Outlined.FavoriteBorder
    n.announcement != null || n.notificationType.equals("ANNOUNCEMENT", true) -> Icons.Outlined.Campaign
    n.notificationType.equals("FEEDBACK", true) -> Icons.Outlined.RateReview
    else -> Icons.Outlined.Notifications
}

@Composable
fun NotificationsScreen(user: User) {
    val repo = LocalAppContainer.current.repository
    val actions = rememberActionRunner()
    val loader = rememberLoader(user.id) { repo.notifications(user.id!!) }

    LoadedList(loader) { notifications ->
        item {
            PageHeading("UPDATES", "Notifications", "Homework, missions and announcements from your classes.")
            if (notifications.any { it.isUnread }) {
                LinkButton("Mark all as read") {
                    actions.run(onSuccess = loader.reload) { repo.markAllRead(user.id!!) }
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                EduCard {
                    EmptyState(Icons.Outlined.NotificationsNone, "You're all caught up", "New activity will show up here.")
                }
            }
        }

        items(notifications, key = { it.id ?: it.hashCode().toLong() }) { n ->
            EduCard(onClick = if (n.isUnread) {
                { actions.run(onSuccess = loader.reload) { repo.markRead(n) } }
            } else null) {
                Row(verticalAlignment = Alignment.Top) {
                    IconTile(iconFor(n))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(n.message ?: "Notification", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(2.dp))
                        val context = n.homework?.title ?: n.mission?.title ?: n.announcement?.title
                        if (context != null && context != n.message) {
                            Text(context, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(Dates.formatDateTime(n.sentAt), style = MaterialTheme.typography.bodySmall, color = EduColors.TextLight)
                    }
                    if (n.isUnread) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EduColors.Danger),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(user: User) {
    val container = LocalAppContainer.current
    val actions = rememberActionRunner()

    var editing by rememberSaveable { mutableStateOf(false) }
    var firstName by rememberSaveable(user) { mutableStateOf(user.firstName ?: "") }
    var lastName by rememberSaveable(user) { mutableStateOf(user.lastName ?: "") }
    var phone by rememberSaveable(user) { mutableStateOf(user.phoneNumber ?: "") }
    var language by rememberSaveable(user) { mutableStateOf(user.preferredLanguage ?: "") }

    val loader = rememberLoader(Unit) { user }

    LoadedList(loader) {
        item { PageHeading("ACCOUNT", "Profile", "Your EduBridge account details.") }

        item {
            EduCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(user.initials, 56.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(user.fullName, style = MaterialTheme.typography.titleLarge)
                        Text(user.email ?: "", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(6.dp))
                        StatusChip(user.roleLabel, ChipTone.Primary)
                    }
                }
            }
        }

        item {
            EduCard {
                if (!editing) {
                    InfoRow("Phone number", user.phoneNumber ?: "Not set")
                    InfoRow("Preferred language", user.preferredLanguage ?: "Not set")
                    InfoRow("Member since", Dates.formatDate(user.createdAt))
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton("Edit profile", { editing = true }, Modifier.fillMaxWidth())
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        EduTextField("First name", firstName, { firstName = it })
                        EduTextField("Last name", lastName, { lastName = it })
                        EduTextField("Phone number", phone, { phone = it }, keyboardType = KeyboardType.Phone)
                        EduTextField("Preferred language", language, { language = it }, placeholder = "e.g. English, Filipino")
                        PrimaryButton(
                            "Save changes",
                            loading = actions.busy,
                            enabled = firstName.isNotBlank() && lastName.isNotBlank(),
                            onClick = {
                                actions.run("Profile updated", onSuccess = { editing = false }) {
                                    val saved = container.repository.updateProfile(
                                        user.copy(
                                            firstName = firstName.trim(),
                                            lastName = lastName.trim(),
                                            phoneNumber = phone.trim().ifBlank { null },
                                            preferredLanguage = language.trim().ifBlank { null },
                                        ),
                                    )
                                    container.session.updateUser(saved)
                                }
                            },
                        )
                        SecondaryButton("Cancel", { editing = false }, Modifier.fillMaxWidth())
                    }
                }
            }
        }

        item {
            SecondaryButton(
                "Log out",
                onClick = { container.session.logout() },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Outlined.Logout,
            )
        }

        item {
            Text(
                "Server: ${BuildConfig.API_BASE_URL}",
                style = MaterialTheme.typography.bodySmall,
                color = EduColors.TextLight,
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.titleSmall)
    }
}
