package ro.sts.dgc.update

import com.google.android.play.core.appupdate.AppUpdateManager

/**
 * Wraps [AppUpdateManager] interaction.
 * The update wrapper is designed to be a single-use object. It carries out the workflow using host
 * [androidx.lifecycle.Lifecycle] and terminates in either [AppUpdateView.updateComplete] or [AppUpdateView.updateFailed].
 * [AppUpdateManager] pops up activities-for-result from time to time.
 * To check if the activity result belongs to update flow call [checkActivityResult] function of update wrapper in your hosting activity.
 */
interface AppUpdateWrapper {
    companion object {
        /**
         * Originally, updating a list of subscribers within event dispatching crashes AppUpdateManager with concurrent update exception.
         * If your application uses several listeners simultaneously (like you have multiple activity setup) or you encounter such an exception - set this value to true
         * @see AppUpdateManager.registerListener
         * @see AppUpdateManager.unregisterListener
         */
        var USE_SAFE_LISTENERS = false

        /**
         * The request code wrapper uses to run [AppUpdateManager.startUpdateFlowForResult]
         */
        var REQUEST_CODE_UPDATE = 1050
    }

    /**
     * Checks activity result and returns `true` if result is an update result and was handled
     * Use to check update activity result in [android.app.Activity.onActivityResult]
     */
    fun checkActivityResult(requestCode: Int, resultCode: Int): Boolean

    /**
     * Cancels update installation
     * Call when update is downloaded and user cancelled app restart
     * Effective if update is called with [com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE]
     */
    fun userCanceledUpdate()

    /**
     * Completes update
     * Call when update is downloaded and user confirmed app restart
     * Effective if update is called with [com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE]
     */
    fun userConfirmedUpdate()

    /**
     * Stops update workflow and cleans-up
     */
    fun cleanup()
}