package com.it342g02.edubridge.mobile.data.remote

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.it342g02.edubridge.mobile.BuildConfig
import com.it342g02.edubridge.mobile.data.session.TokenStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Builds the Retrofit client. Mirrors src/api/client.js:
 *  - attaches "Authorization: Bearer <token>" when a token is stored
 *  - a 401 while holding a token means it expired or is invalid -> clear it and log out
 */
object ApiClient {

    fun create(tokenStore: TokenStore, onUnauthorized: () -> Unit): EduBridgeApi {
        val authInterceptor = Interceptor { chain ->
            val token = tokenStore.cachedToken
            val request = if (token != null) {
                chain.request().newBuilder().header("Authorization", "Bearer $token").build()
            } else {
                chain.request()
            }

            val response = chain.proceed(request)

            if (response.code == 401 && token != null) {
                tokenStore.clearCacheNow()
                onUnauthorized()
            }

            response
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().create() // nulls are skipped, so {"id": 3} references stay minimal

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(EduBridgeApi::class.java)
    }
}

/**
 * Turns any failure into a message fit for the UI. Spring returns {"message": "..."}
 * (server.error.include-message=always), which is what the web app shows too.
 */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> {
        val body = try {
            response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        val serverMessage = try {
            body?.let { JsonParser.parseString(it).asJsonObject.get("message")?.asString }
        } catch (_: Exception) {
            null
        }
        when {
            !serverMessage.isNullOrBlank() -> serverMessage
            code() == 401 -> "Your session has expired. Please sign in again."
            code() == 403 -> "You don't have access to that."
            code() == 404 -> "That item no longer exists."
            else -> "Request failed (${code()})."
        }
    }
    is IOException -> "Unable to reach the server. Please try again."
    else -> message ?: "Something went wrong."
}
