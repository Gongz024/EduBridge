package com.it342g02.edubridge.mobile.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.it342g02.edubridge.mobile.AppContainer
import com.it342g02.edubridge.mobile.data.remote.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val LocalAppContainer = staticCompositionLocalOf<AppContainer> { error("AppContainer not provided") }
val LocalSnackbar = staticCompositionLocalOf<SnackbarHostState> { error("Snackbar host not provided") }

sealed interface Load<out T> {
    data object Loading : Load<Nothing>
    data class Error(val message: String) : Load<Nothing>
    data class Ready<T>(val data: T) : Load<T>
}

@Stable
class Loader<T>(val state: Load<T>, val isRefreshing: Boolean, val reload: () -> Unit) {
    val data: T? get() = (state as? Load.Ready<T>)?.data
}

/**
 * Runs [block] when the keys change or when reload() is called. Data already on
 * screen stays visible while a reload is in flight, so pull-to-refresh doesn't flash.
 */
@Composable
fun <T> rememberLoader(vararg keys: Any?, block: suspend () -> T): Loader<T> {
    var version by remember(*keys) { mutableIntStateOf(0) }
    var state by remember(*keys) { mutableStateOf<Load<T>>(Load.Loading) }
    var refreshing by remember(*keys) { mutableStateOf(false) }

    LaunchedEffect(*keys, version) {
        refreshing = state is Load.Ready
        state = try {
            Load.Ready(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (state is Load.Ready) {
                state // keep showing the last good data
            } else {
                Load.Error(e.toUserMessage())
            }
        }
        refreshing = false
    }

    return Loader(state, refreshing) { version++ }
}

/** Runs one write at a time and reports success/failure through the snackbar. */
@Stable
class ActionRunner(private val scope: CoroutineScope, private val snackbar: SnackbarHostState) {
    var busy by mutableStateOf(false)
        private set

    fun run(successMessage: String? = null, onSuccess: () -> Unit = {}, block: suspend () -> Unit) {
        if (busy) return
        busy = true
        scope.launch {
            try {
                block()
                onSuccess()
                successMessage?.let { snackbar.showSnackbar(it) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                snackbar.showSnackbar(e.toUserMessage())
            } finally {
                busy = false
            }
        }
    }
}

@Composable
fun rememberActionRunner(): ActionRunner {
    val scope = rememberCoroutineScope()
    val snackbar = LocalSnackbar.current
    return remember(scope, snackbar) { ActionRunner(scope, snackbar) }
}
