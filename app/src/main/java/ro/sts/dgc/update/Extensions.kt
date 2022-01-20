package ro.sts.dgc.update

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallErrorCode
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import ro.sts.dgc.update.AppUpdateWrapper.Companion.USE_SAFE_LISTENERS

/**
 * AppUpdateInfo logging format
 */
private const val APP_UPDATE_INFO_FORMAT = """Update info: 
    - available version code: %d
    - update availability: %s
    - install status: %s
    - update types allowed: %s"""

/**
 * Formats update info into human-readable format
 */
fun AppUpdateInfo.format(): String = APP_UPDATE_INFO_FORMAT.format(
    availableVersionCode(),
    formatUpdateAvailability(),
    formatInstallStatus(),
    formatUpdateTypesAllowed()
)

/**
 * Returns a constant name for update availability
 */
private fun AppUpdateInfo.formatUpdateAvailability(): String = when (updateAvailability()) {
    UpdateAvailability.UNKNOWN -> "UNKNOWN"
    UpdateAvailability.UPDATE_NOT_AVAILABLE -> "UPDATE_NOT_AVAILABLE"
    UpdateAvailability.UPDATE_AVAILABLE -> "UPDATE_AVAILABLE"
    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> "DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS"
    else -> "UNKNOWN UPDATE AVAILABILITY: ${updateAvailability()}"
}

/**
 * Returns a constant name for update status
 */
private fun AppUpdateInfo.formatInstallStatus(): String = formatInstallStatus(installStatus())

/**
 * Returns a constant name for update status
 * @param status Install status
 */
fun formatInstallStatus(status: Int): String = when (status) {
    InstallStatus.UNKNOWN -> "UNKNOWN"
    InstallStatus.PENDING -> "PENDING"
    InstallStatus.DOWNLOADING -> "DOWNLOADING"
    InstallStatus.DOWNLOADED -> "DOWNLOADED"
    InstallStatus.INSTALLING -> "INSTALLING"
    InstallStatus.INSTALLED -> "INSTALLED"
    InstallStatus.FAILED -> "FAILED"
    InstallStatus.CANCELED -> "CANCELED"
    else -> "UNKNOWN INSTALL STATUS: $status"
}

/**
 * Retrieves allowed update types
 */
@VisibleForTesting
fun AppUpdateInfo.formatUpdateTypesAllowed(): String {
    val result = mutableListOf<String>()
    if (isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
        result.add("FLEXIBLE")
    }
    if (isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
        result.add("IMMEDIATE")
    }
    return result.takeIf { it.isNotEmpty() }?.joinToString() ?: "NONE"
}

/**
 * Returns a constant name for installation error
 * @param code Installation error code
 */
@Suppress("DEPRECATION")
fun formatInstallErrorCode(code: Int): String = when (code) {
    InstallErrorCode.NO_ERROR -> "NO_ERROR"
    InstallErrorCode.NO_ERROR_PARTIALLY_ALLOWED -> "NO_ERROR_PARTIALLY_ALLOWED"
    InstallErrorCode.ERROR_UNKNOWN -> "ERROR_UNKNOWN"
    InstallErrorCode.ERROR_API_NOT_AVAILABLE -> "ERROR_API_NOT_AVAILABLE"
    InstallErrorCode.ERROR_INVALID_REQUEST -> "ERROR_INVALID_REQUEST"
    InstallErrorCode.ERROR_INSTALL_UNAVAILABLE -> "ERROR_INSTALL_UNAVAILABLE"
    InstallErrorCode.ERROR_INSTALL_NOT_ALLOWED -> "ERROR_INSTALL_UNAVAILABLE"
    InstallErrorCode.ERROR_DOWNLOAD_NOT_PRESENT -> "ERROR_DOWNLOAD_NOT_PRESENT"
    InstallErrorCode.ERROR_APP_NOT_OWNED -> "ERROR_APP_NOT_OWNED"
    InstallErrorCode.ERROR_INTERNAL_ERROR -> "ERROR_INTERNAL_ERROR"
    InstallErrorCode.ERROR_PLAY_STORE_NOT_FOUND -> "ERROR_PLAY_STORE_NOT_FOUND"
    else -> "UNKNOWN INSTALL ERROR: $code"
}


/**
 * Safely registers listener
 * Originally, updating a list of subscribers within event dispatching crashes AppUpdateManager dispatcher with concurrent update exception.
 * See test files for explanation
 * @param listener Update state listener
 */
internal fun AppUpdateManager.registerListenerSafe(listener: InstallStateUpdatedListener) {
    if (USE_SAFE_LISTENERS) {
        doRegisterListenerSafe(listener)
    } else {
        registerListener(listener)
    }
}

/**
 * Registers listener in next loop
 */
@VisibleForTesting
internal fun AppUpdateManager.doRegisterListenerSafe(listener: InstallStateUpdatedListener) {
    Handler(Looper.getMainLooper()).post { registerListener(listener) }
}

/**
 * Safely unregisters listener
 * Originally, updating a list of subscribers within event dispatching crashes AppUpdateManager dispatcher with concurrent update exception.
 * See test files for explanation
 * @param listener Update state listener
 */
internal fun AppUpdateManager.unregisterListenerSafe(listener: InstallStateUpdatedListener) {
    if (USE_SAFE_LISTENERS) {
        doUnregisterListenerSafe(listener)
    } else {
        unregisterListener(listener)
    }
}

/**
 * Unregisters listener in next loop
 */
@VisibleForTesting
internal fun AppUpdateManager.doUnregisterListenerSafe(listener: InstallStateUpdatedListener) {
    Handler(Looper.getMainLooper()).post { unregisterListener(listener) }
}