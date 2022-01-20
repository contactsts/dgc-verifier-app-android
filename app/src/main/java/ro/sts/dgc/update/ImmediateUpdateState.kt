package ro.sts.dgc.update

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import ro.sts.dgc.update.AppUpdateException.Companion.ERROR_NO_IMMEDIATE_UPDATE
import ro.sts.dgc.update.AppUpdateException.Companion.ERROR_UPDATE_FAILED
import ro.sts.dgc.update.AppUpdateException.Companion.ERROR_UPDATE_TYPE_NOT_ALLOWED
import ro.sts.dgc.update.AppUpdateWrapper.Companion.REQUEST_CODE_UPDATE
import timber.log.Timber

/**
 * Immediate update flow
 */
internal sealed class ImmediateUpdateState : AppUpdateState() {
    companion object {
        /**
         * Starts immediate update flow
         * @param stateMachine Application update stateMachine state-machine
         */
        fun start(stateMachine: AppUpdateStateMachine) {
            stateMachine.setUpdateState(Initial())
        }
    }

    /**
     * Transfers to checking state
     */
    protected fun checking() {
        setUpdateState(Checking())
    }

    /**
     * Transfers to update state
     */
    protected fun update(appUpdateInfo: AppUpdateInfo) {
        setUpdateState(Update(appUpdateInfo))
    }

    /**
     * Transfers to update ui check
     */
    protected fun updateUiCheck() {
        setUpdateState(UpdateUiCheck())
    }

    /**
     * Initial state
     */
    internal class Initial : ImmediateUpdateState() {
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
    internal class Checking : ImmediateUpdateState() {
        /*
         * Set to true on [onStop] to prevent view interaction
         * as there is no way to abort task
         */
        private var stopped: Boolean = false

        /**
         * Handles lifecycle `onStart`
         */
        override fun onStart() {
            super.onStart()
            Timber.d("In-App Update onStart")
            Timber.i("In-App Update getting application update info for IMMEDIATE update...")
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
            Timber.d("In-App Update failing update due to update check...")
            fail(AppUpdateException(ERROR_UPDATE_FAILED, appUpdateException))
        }

        /**
         * Starts update on success or transfers to failed state
         */
        private fun processUpdateInfo(appUpdateInfo: AppUpdateInfo) {
            Timber.d("In-App Update evaluating update info...")
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE, UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> update(appUpdateInfo)
                else -> fail(AppUpdateException(ERROR_NO_IMMEDIATE_UPDATE))
            }
        }
    }

    /**
     * Updates application
     * @param updateInfo Update info to start immediate update
     */
    internal class Update(private val updateInfo: AppUpdateInfo) : ImmediateUpdateState() {
        /**
         * Handles lifecycle `onResume`
         */
        override fun onResume() {
            super.onResume()
            Timber.d("In-App Update onResume")
            if (!updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                Timber.d("In-App Update type IMMEDIATE is not allowed!")
                fail(AppUpdateException(ERROR_UPDATE_TYPE_NOT_ALLOWED))
            } else withUpdateView {
                Timber.d("In-App Update starting play-core update installer for IMMEDIATE state...")
                // As consent activity starts current activity looses focus.
                // So we need to transfer to the next state to break popup cycle.
                updateUiCheck()

                updateManager.startUpdateFlowForResult(
                    updateInfo,
                    AppUpdateType.IMMEDIATE,
                    activity,
                    REQUEST_CODE_UPDATE
                )
                updateInstallUiVisible()
            }
        }
    }

    /**
     * Checks for update ui errors
     */
    internal class UpdateUiCheck : ImmediateUpdateState() {
        /**
         * Checks activity result and returns `true` if result is an update result and was handled
         * Use to check update activity result in [android.app.Activity.onActivityResult]
         */
        override fun checkActivityResult(requestCode: Int, resultCode: Int): Boolean {
            Timber.d("In-App Update checkActivityResult: requestCode(%d), resultCode(%d)", requestCode, resultCode)
            if (REQUEST_CODE_UPDATE != requestCode) {
                return false
            }

            if (Activity.RESULT_OK == resultCode) {
                Timber.d("In-App Update installation complete")
                complete()
            } else {
                Timber.d("In-App Update failing update due to installation failure...")
                fail(AppUpdateException(ERROR_UPDATE_FAILED))
            }

            return true
        }
    }
}