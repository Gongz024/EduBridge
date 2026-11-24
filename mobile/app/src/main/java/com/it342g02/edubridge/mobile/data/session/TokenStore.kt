package com.it342g02.edubridge.mobile.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "edubridge_auth")

/**
 * Persists the JWT, the mobile equivalent of the web app's localStorage "edubridge.token".
 * The current value is also cached in memory so the OkHttp interceptor can read it
 * synchronously on every request.
 */
class TokenStore(private val context: Context) {
    private val key = stringPreferencesKey("edubridge.token")

    @Volatile
    var cachedToken: String? = null
        private set

    suspend fun load(): String? {
        cachedToken = context.authDataStore.data.map { it[key] }.first()
        return cachedToken
    }

    suspend fun save(token: String) {
        cachedToken = token
        context.authDataStore.edit { it[key] = token }
    }

    suspend fun clear() {
        cachedToken = null
        context.authDataStore.edit { it.remove(key) }
    }

    /** Used from the network thread when the server rejects the token. */
    fun clearCacheNow() {
        cachedToken = null
    }
}
