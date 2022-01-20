package ro.sts.dgc.update

import android.app.Activity
import androidx.annotation.CallSuper
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.*
import ro.sts.dgc.update.AppUpdateException.Companion.ERROR_UNKNOWN_UPDATE_RESULT
import ro.sts.dgc.update.AppUpdateException.Companion.ERROR_UPDATE_FAILED
import ro.sts.dgc.update.AppUpdateException.Companion.ERROR_UPDATE_TYPE_NOT_ALLOWED
import ro.sts.dgc.update.AppUpdateWrapper.Companion.REQUEST_CODE_UPDATE
import timber.log.Timber

/**
 * Flexible update flow
 */
internal sealed class FlexibleUpdateState : AppUpdateState() {
    companion object {
        /**
         * Starts flexible update flow
         * @param stateMachine Application update stateMachine state-machine
         */
        fun start(stateMachine: AppUpdateStateMachine) {
            stateMachine.setUpdateState(Initial())
        }
    }

    /**
     * Transfers to update-checking state
     */
    protected fun checking() {
        setUpdateState(Checking())
    }

    /**
     * Transfers to update-consent state
     */
    protected fun updateConsent(appUpdateInfo: AppUpdateInfo) {
        setUpdateState(UpdateConsent(appUpdateInfo))
    }

    /**
     * Transfers to update consent check
     */
    protected fun updateConsentCheck() {
        setUpdateState(UpdateConsentCheck())
    }


    /**
     * Transfers to install consent check
     */
    protected fun installConsentCheck() {
        setUpdateState(InstallConsentCheck())
    }

    /**
     * Transfers to downloading state
     */
    protected fun downloading() {
        setUpdateState(Downloading())
    }

    /**
     * Transfers to install-consent state
     */
    protected fun installConsent() {
        setUpdateState(InstallConsent())
    }

    /**
     * Transfers to complete-update state
     */
    protected fun completeUpdate() {
        setUpdateState(CompleteUpdate())
    }

    /**
     * According to:
     * https://developer.android.com/reference/android/app/Activity.html#onActivityResult(int,%2520int,%2520android.content.Intent)
     * `onActivityResult` will be called before `onResume` thus saving explicit cancellation before any UI interaction takes place.
     * This may prevent download consent popup if activity was recreated during consent display
     */
    @CallSuper
    override fun checkActivityResult(requestCode: Int, resultCode: Int): Boolean {
        Timber.d("In-App Update checkActivityResult: requestCode(%d), resultCode(%d)", requestCode, resultCode)
        return if (REQUEST_CODE_UPDATE == requestCode && Activity.RESULT_CANCELED == resultCode) {
            Timber.d("In-App Update download cancelled")
            markUserCancelTime()
            complete()
            true
        } else {
            false
        }
    }

    /**
     * Initial state
     */
    internal class Initial : FlexibleUpdateState() {
        /**
         * Handles lifecycle `onStart`
         */
        override fun onStart() {
            super.onStart()
            Timber.d("In-App Update onStart")
            checking()
        }
    }

    /**
     * Checks for update
     */
    internal class Checking : FlexibleUpdateState() {
        /*
         * Set to true on [onStop] to prevent view interaction as there is no way to abort task
         */
        private var stopped: Boolean = false

        /**
         * Handles lifecycle `onStart`
         */
        override fun onStart() {
            super.onStart()
            Timber.d("In-App Update  onStart")
            ifNotBroken {
                Timber.i("In-App Update getting application update info for FLEXIBLE update...")
                withUpdateView {
                    updateChecking()
                }
                updateManager.appUpdateInfo
                    .addOnSuccessListener {
                        Timber.i("In-App Update retrieved update info: %s", it.format())
                        if (!stopped) {
                            processUpdateInfo(it)
                        }
                    }
                    .addOnFailureListener {
                        Timber.w(it, "In-App Update error getting application update info: ")
                        if (!stopped) {
                            reportUpdateCheckFailure(it)
                        }
                    }
            }
        }

        /**
         * Called by state-machine when state is being replaced
         */
        override fun cleanup() {
            super.cleanup()
            Timber.d("In-App Update cleanup")
            stopped = true
        }

        /**
         * Handles lifecycle `onStop`
         */
        override fun onStop() {
            super.onStop()
            Timber.d("In-App Update onStop")
            complete()
        }

        /**
         * Transfers to failed state
         */
        private fun reportUpdateCheckFailure(appUpdateException: Throwable) {
            Timber.d("In-App Update reporting update error due to update check...")
            reportError(AppUpdateException(AppUpdateException.ERROR_UPDATE_CHECK_FAILED, appUpdateException))
        }

        /**
         * Starts update on success or transfers to failed state
         */
        private fun processUpdateInfo(appUpdateInfo: AppUpdateInfo) {
            Timber.d("In-App Update evaluating update info...")
            with(appUpdateInfo) {
                when (updateAvailability()) {
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> when (installStatus()) {
                        InstallStatus.PENDING, InstallStatus.DOWNLOADING -> downloading()
                        InstallStatus.DOWNLOADED -> installConsent()
                        InstallStatus.INSTALLING -> completeUpdate()
                        else -> complete()
                    }
                    UpdateAvailability.UPDATE_AVAILABLE -> updateConsent(appUpdateInfo)
                    else -> complete()
                }
            }
        }
    }

    /**
     * Opens update consent.
     * View should handle pass activity result to [checkActivityResult]
     * @param updateInfo Update info to start flexible update
     */
    internal class UpdateConsent(private val updateInfo: AppUpdateInfo) : FlexibleUpdateState() {
        /**
         * Handles lifecycle `onResume`
         */
        override fun onResume() {
            super.onResume()
            Timber.d("In-App Update onResume")
            ifNotBroken(updateInfo) {
                if (!updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    Timber.d("In-App Update type FLEXIBLE is not allowed!")
                    reportError(AppUpdateException(ERROR_UPDATE_TYPE_NOT_ALLOWED))
                } else withUpdateView {
                    Timber.d("In-App Update asking for installation consent...")
                    // As consent activity starts current activity looses focus.
                    // So we need to transfer to the next state to break popup cycle.
                    updateConsentCheck()

                    stateMachine.updateManager.startUpdateFlowForResult(
                        updateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        REQUEST_CODE_UPDATE
                    )
                }
            }
        }
    }

    /**
     * Checks for consent activity result
     */
    internal class UpdateConsentCheck : FlexibleUpdateState() {
        /**
         * Checks activity result and returns `true` if result is an update result and was handled
         * Use to check update activity result in [android.app.Activity.onActivityResult]
         */
        override fun checkActivityResult(requestCode: Int, resultCode: Int): Boolean = when {
            super.checkActivityResult(requestCode, resultCode) -> true
            REQUEST_CODE_UPDATE != requestCode -> false
            else -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Timber.d("In-App Update user accepted update")
                        downloading()
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        Timber.d("In-App Update reporting update error due to play-core UI error...")
                        reportError(AppUpdateException(ERROR_UPDATE_FAILED))
                    }
                    else -> {
                        Timber.w("In-App Update failing due to unknown play-core UI result...")
                        reportError(AppUpdateException(ERROR_UNKNOWN_UPDATE_RESULT))
                    }
                }
                true
            }
        }
    }

    /**
     * Watches for update download status
     */
    internal class Downloading : FlexibleUpdateState() {
        /**
         * Update state listener
         */
        private val listener = InstallStateUpdatedListener { state ->
            Timber.d("In-App Update install state updated: %s", formatInstallStatus(state.installStatus()))
            when (state.installStatus()) {
                InstallStatus.INSTALLED -> complete()
                InstallStatus.CANCELED -> {
                    markUserCancelTime()
                    complete()
                }
                InstallStatus.DOWNLOADED -> installConsent()
                InstallStatus.INSTALLING -> completeUpdate()
                InstallStatus.FAILED -> {
                    val errorCode = state.installErrorCode()
                    Timber.d("In-App Update install error code: %s", formatInstallErrorCode(errorCode))
                    reportError(
                        AppUpdateException(
                            when (state.installErrorCode()) {
                                InstallErrorCode.ERROR_INSTALL_UNAVAILABLE, InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED -> ERROR_UPDATE_TYPE_NOT_ALLOWED
                                else -> ERROR_UPDATE_FAILED
                            }
                        )
                    )
                }
                else -> Timber.w("Unexpected install status: %s", formatInstallStatus(state.installStatus()))
            }
        }

        /**
         * Handles lifecycle `onStart`
         */
        override fun onStart() {
            super.onStart()
            Timber.d("In-App Update onStart")
            withUpdateView {
                updateDownloadStarts()
            }
        }

        /**
         * Handles lifecycle `onResume`
         */
        override fun onResume() {
            super.onResume()
            Timber.d("In-App Update onResume")
            Timber.d("In-App Update registering to installation state updates...")
            updateManager.registerListenerSafe(listener)
        }

        /**
         * Handles lifecycle `onPause`
         */
        override fun onPause() {
            super.onPause()
            Timber.d("In-App Update onPause")
            // Switch back to checking so only the topmost activity handle installation progress.
            checking()
        }

        /**
         * Called by state-machine when state is being replaced
         */
        override fun cleanup() {
            super.cleanup()
            Timber.d("In-App Update cleanup")
            Timber.d("In-App Update unregistering from installation state updates...")
            updateManager.unregisterListenerSafe(listener)
        }
    }

    /**
     * Instructs view to display update consent
     */
    internal class InstallConsent : FlexibleUpdateState() {
        /**
         * Handles lifecycle `onResume`
         */
        override fun onResume() {
            super.onResume()
            Timber.d("In-App Update onResume")
            ifNotBroken {
                Timber.d("In-App Update getting installation consent...")
                withUpdateView {
                    // As consent activity starts current activity looses focus.
                    // So we need to transfer to the next state to break popup cycle.
                    installConsentCheck()
                    updateReady()
                }
            }
        }

    }

    /**
     * Listens to install consent results
     */
    internal class InstallConsentCheck : FlexibleUpdateState() {
        /**
         * Completes update
         * Call when update is downloaded and user confirmed app restart
         * Effective if update is called with [com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE]
         */
        override fun userConfirmedUpdate() {
            Timber.d("In-App Update user confirms update")
            completeUpdate()
        }

        /**
         * Cancels update installation
         * Call when update is downloaded and user cancelled app restart
         * Effective if update is called with [com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE]
         */
        override fun userCanceledUpdate() {
            Timber.d("In-App Update user cancels update")
            markUserCancelTime()
            complete()
        }
    }

    /**
     * Completes flexible update
     */
    internal class CompleteUpdate : FlexibleUpdateState() {
        /*
         * Set to true on [onStop] to prevent view interaction as there is no way to abort task
         */
        private var stopped: Boolean = false

        /**
         * Handles lifecycle `onStart`
         */
        override fun onStart() {
            super.onStart()
            Timber.d("In-App Update onStart")
            Timber.d("In-App Update starting play-core update installer for FLEXIBLE state...")
            updateManager
                .completeUpdate()
                .addOnSuccessListener {
                    Timber.d("In-App Update update installation complete")
                    if (!stopped) {
                        complete()
                    }
                }
                .addOnFailureListener {
                    Timber.d("In-App Update reporting update error due to installation failure...")
                    if (!stopped) {
                        reportError(AppUpdateException(ERROR_UPDATE_FAILED, it))
                    }
                }
            withUpdateView {
                updateInstallUiVisible()
            }
        }

        /**
         * Called by state-machine when state is being replaced
         */
        override fun cleanup() {
            super.cleanup()
            stopped = true
        }

        /**
         * Handles lifecycle `onStop`
         */
        override fun onStop() {
            super.onStop()
            complete()
        }
    }
}