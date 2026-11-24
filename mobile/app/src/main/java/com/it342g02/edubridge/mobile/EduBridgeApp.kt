package com.it342g02.edubridge.mobile

import android.app.Application
import com.it342g02.edubridge.mobile.data.remote.ApiClient
import com.it342g02.edubridge.mobile.data.remote.EduBridgeApi
import com.it342g02.edubridge.mobile.data.repository.EduBridgeRepository
import com.it342g02.edubridge.mobile.data.session.SessionManager
import com.it342g02.edubridge.mobile.data.session.TokenStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Simple manual dependency container; one instance per process. */
class AppContainer(app: Application) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val tokenStore = TokenStore(app)
    val session = SessionManager(tokenStore, appScope)
    val api: EduBridgeApi = ApiClient.create(tokenStore) {
        // the interceptor runs on an OkHttp thread; hop to the main scope to update UI state
        appScope.launch { session.onUnauthorized() }
    }
    val repository = EduBridgeRepository(api)

    init {
        session.api = api
    }
}

class EduBridgeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.session.restore()
    }
}
