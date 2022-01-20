package ro.sts.dgc.update

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.play.core.appupdate.AppUpdateManager
import timber.log.Timber

/**
 * App update state machine
 */
internal interface AppUpdateStateMachine {
    /**
     * [AppUpdateManager] instance
     */
    val updateManager: AppUpdateManager

    /**
     * Terminates update flow if user has already cancelled an update
     */
    val flowBreaker: UpdateFlowBreaker

    /**
     * Update view
     */
    val view: AppUpdateView

    /**
     * Sets new update state
     */
    fun setUpdateState(newState: AppUpdateState)
}

/**
 * Manages state transition and component lifecycle
 * @param lifecycle Component lifecycle
 * @param updateManager AppUpdateManager instance
 * @param view Application update view interface
 * @param flowBreaker Terminates update flow if user has already cancelled an update
 */
internal class AppUpdateLifecycleStateMachine(
    private val lifecycle: Lifecycle,
    override val updateManager: AppUpdateManager,
    override val view: AppUpdateView,
    override val flowBreaker: UpdateFlowBreaker = UpdateFlowBreaker.alwaysOn()
) : AppUpdateStateMachine, AppUpdateWrapper, LifecycleObserver {
    /**
     * Current update state
     */
    @VisibleForTesting
    var currentUpdateState: AppUpdateState

    init {
        currentUpdateState = None()
        lifecycle.addObserver(this)
        Timber.d("In-App Update state machine initialized")
    }

    /**
     * Sets new update state
     */
    override fun setUpdateState(newState: AppUpdateState) {
        Timber.d("In-App Update setting new state: %s", newState.javaClass.simpleName)
        currentUpdateState.cleanup()

        newState.stateMachine = this
        currentUpdateState = newState

        with(lifecycle.currentState) {
            if (isAtLeast(Lifecycle.State.STARTED)) {
                Timber.d("In-App Update starting new state...")
                newState.onStart()
            }
            if (isAtLeast(Lifecycle.State.RESUMED)) {
                Timber.d("In-App Update resuming new state...")
                newState.onResume()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        currentUpdateState.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        currentUpdateState.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onPause() {
        currentUpdateState.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        currentUpdateState.onStop()
    }

    /**
     * Checks activity result and returns `true` if result is an update result and was handled
     * Use to check update activity result in [android.app.Activity.onActivityResult]
     */
    override fun checkActivityResult(requestCode: Int, resultCode: Int): Boolean {
        Timber.d("In-App Update processing activity result: requestCode(%d), resultCode(%d)", requestCode, resultCode)
        return currentUpdateState.checkActivityResult(requestCode, resultCode).also {
            Timber.d("In-App Update activity result handled: %b", it)
        }
    }

    /**
     * Cancels update installation
     * Call when update is downloaded and user cancelled app restart
     * Effective if update is called with [com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE]
     */
    override fun userCanceledUpdate() {
        currentUpdateState.userCanceledUpdate()
    }

    /**
     * Completes update
     * Call when update is downloaded and user confirmed app restart
     * Effective if update is called with [com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE]
     */
    override fun userConfirmedUpdate() {
        currentUpdateState.userConfirmedUpdate()
    }

    /**
     * Stops update workflow and cleans-up
     */
    override fun cleanup() {
        lifecycle.removeObserver(this)
        currentUpdateState = None()
        Timber.d("In-App Update cleaned-up!")
    }
}