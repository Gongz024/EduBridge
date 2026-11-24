package com.it342g02.edubridge.mobile.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.it342g02.edubridge.mobile.data.model.Role
import com.it342g02.edubridge.mobile.data.model.SignupRequest
import com.it342g02.edubridge.mobile.data.remote.toUserMessage
import com.it342g02.edubridge.mobile.ui.components.BrandMark
import com.it342g02.edubridge.mobile.ui.components.EduCard
import com.it342g02.edubridge.mobile.ui.components.EduTextField
import com.it342g02.edubridge.mobile.ui.components.FormError
import com.it342g02.edubridge.mobile.ui.components.LocalAppContainer
import com.it342g02.edubridge.mobile.ui.components.PrimaryButton
import com.it342g02.edubridge.mobile.ui.theme.EduColors
import com.it342g02.edubridge.mobile.ui.theme.EduRadius
import kotlinx.coroutines.launch

private const val MIN_PASSWORD_LENGTH = 8 // same rule as the web Signup page and UserService

/** .login-page: centered card on the warm background. */
@Composable
private fun AuthPage(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(EduColors.Background)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        EduCard(padding = androidx.compose.foundation.layout.PaddingValues(24.dp)) {
            content()
        }
    }
}

@Composable
private fun AuthBrand() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BrandMark()
        Spacer(Modifier.width(12.dp))
        Column {
            Text("EduBridge", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Teachers · Parents · Students", style = MaterialTheme.typography.bodySmall)
        }
    }
    Spacer(Modifier.height(24.dp))
}

@Composable
private fun AuthSwitch(prompt: String, action: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text("$prompt ", style = MaterialTheme.typography.bodySmall)
        Text(
            action,
            style = MaterialTheme.typography.bodySmall,
            color = EduColors.Primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}

@Composable
fun LoginScreen(onCreateAccount: () -> Unit) {
    val session = LocalAppContainer.current.session
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var submitting by rememberSaveable { mutableStateOf(false) }

    AuthPage {
        AuthBrand()
        Text("Sign in", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Use your EduBridge account to continue.", style = MaterialTheme.typography.bodyMedium, color = EduColors.TextMuted)
        Spacer(Modifier.height(18.dp))

        EduTextField("Email", email, { email = it }, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        EduTextField("Password", password, { password = it }, isPassword = true)

        FormError(error)
        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = "Sign in",
            loading = submitting,
            enabled = email.isNotBlank() && password.isNotBlank(),
            onClick = {
                error = null
                submitting = true
                scope.launch {
                    try {
                        session.login(email, password) // navigation reacts to the session state
                    } catch (e: Exception) {
                        error = e.toUserMessage()
                        submitting = false
                    }
                }
            },
        )

        AuthSwitch("New to EduBridge?", "Create an account", onCreateAccount)
    }
}

private data class RoleOption(val role: Role, val label: String, val blurb: String)

private val roleOptions = listOf(
    RoleOption(Role.TEACHER, "Teacher", "Manage classes"),
    RoleOption(Role.PARENT, "Parent", "Follow your child"),
    RoleOption(Role.STUDENT, "Student", "Learn & earn"),
)

@Composable
private fun RolePicker(selected: Role, onSelect: (Role) -> Unit) {
    Text("I am a", style = MaterialTheme.typography.labelMedium)
    Spacer(Modifier.height(6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        roleOptions.forEach { option ->
            val active = option.role == selected
            val shape = RoundedCornerShape(EduRadius.Md)
            Column(
                Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(if (active) EduColors.PrimaryLight else EduColors.InputBackground)
                    .border(1.dp, if (active) EduColors.Primary else EduColors.Border, shape)
                    .clickable { onSelect(option.role) }
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    when (option.role) {
                        Role.TEACHER -> Icons.Outlined.School
                        Role.PARENT -> Icons.Outlined.FamilyRestroom
                        Role.STUDENT -> Icons.Outlined.Face
                    },
                    null,
                    tint = if (active) EduColors.PrimaryDark else EduColors.TextMuted,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    option.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (active) EduColors.PrimaryDark else EduColors.Text,
                )
                Text(option.blurb, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun SignupScreen(onSignIn: () -> Unit) {
    val session = LocalAppContainer.current.session
    val scope = rememberCoroutineScope()

    var role by rememberSaveable { mutableStateOf(Role.TEACHER) }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var submitting by rememberSaveable { mutableStateOf(false) }

    AuthPage {
        AuthBrand()
        Text("Create your account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(4.dp))
        Text("Set up an account to start using EduBridge.", style = MaterialTheme.typography.bodyMedium, color = EduColors.TextMuted)
        Spacer(Modifier.height(18.dp))

        RolePicker(role) { role = it }
        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EduTextField("First name", firstName, { firstName = it }, Modifier.weight(1f))
            EduTextField("Last name", lastName, { lastName = it }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        EduTextField("Email", email, { email = it }, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        EduTextField("Phone number (optional)", phone, { phone = it }, keyboardType = KeyboardType.Phone)
        Spacer(Modifier.height(14.dp))
        EduTextField("Password", password, { password = it }, isPassword = true)
        Spacer(Modifier.height(14.dp))
        EduTextField("Confirm password", confirm, { confirm = it }, isPassword = true)

        FormError(error)
        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = "Create account",
            loading = submitting,
            enabled = firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
            onClick = {
                error = when {
                    password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters."
                    password != confirm -> "Passwords do not match."
                    else -> null
                }
                if (error != null) return@PrimaryButton

                submitting = true
                scope.launch {
                    try {
                        session.signup(
                            SignupRequest(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                email = email.trim(),
                                password = password,
                                role = role,
                                phoneNumber = phone.trim().ifBlank { null },
                            ),
                        )
                    } catch (e: Exception) {
                        error = e.toUserMessage()
                        submitting = false
                    }
                }
            },
        )

        AuthSwitch("Already have an account?", "Sign in", onSignIn)
    }
}
