package com.it342g02.edubridge.mobile.data.session

import com.it342g02.edubridge.mobile.data.model.LoginRequest
import com.it342g02.edubridge.mobile.data.model.SignupRequest
import com.it342g02.edubridge.mobile.data.model.User
import com.it342g02.edubridge.mobile.data.remote.EduBridgeApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SessionState {
    data object Restoring : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val user: User) : SessionState
}

/**
 * Mobile counterpart of the web AuthProvider. Unlike the web workspace (teachers only),
 * every role can sign in here; the UI switches on user.role.
 */
class SessionManager(
    private val tokenStore: TokenStore,
    private val appScope: CoroutineScope,
) {
    lateinit var api: EduBridgeApi

    private val _state = MutableStateFlow<SessionState>(SessionState.Restoring)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    val currentUser: User?
        get() = (_state.value as? SessionState.SignedIn)?.user

    /** Restore the session from a stored token, like the web app's /auth/me check. */
    fun restore() {
        appScope.launch {
            val token = tokenStore.load()
            if (token == null) {
                _state.value = SessionState.SignedOut
                return@launch
            }
            _state.value = try {
                SessionState.SignedIn(api.me())
            } catch (_: Exception) {
                // a 401 already cleared the token; for network errors stay signed out
                tokenStore.clear()
                SessionState.SignedOut
            }
        }
    }

    suspend fun login(email: String, password: String): User {
        val response = api.login(LoginRequest(email.trim(), password))
        return complete(response.token, response.user)
    }

    suspend fun signup(request: SignupRequest): User {
        val response = api.signup(request)
        return complete(response.token, response.user)
    }

    private suspend fun complete(token: String?, user: User?): User {
        if (token.isNullOrBlank() || user == null) error("Unexpected response from the server.")
        tokenStore.save(token)
        _state.value = SessionState.SignedIn(user)
        return user
    }

    /** Called after the profile is edited so the header shows the new name. */
    fun updateUser(user: User) {
        if (_state.value is SessionState.SignedIn) _state.value = SessionState.SignedIn(user)
    }

    fun logout() {
        _state.value = SessionState.SignedOut
        appScope.launch { tokenStore.clear() }
    }

    /** Invoked by the HTTP layer when the API rejects the stored token. */
    fun onUnauthorized() {
        if (_state.value is SessionState.SignedIn) logout()
    }
}
