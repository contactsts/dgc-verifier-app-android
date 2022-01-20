package ro.sts.dgc.update

/**
 * Application update exception
 * @param message Message
 * @param cause Original error
 */
class AppUpdateException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
    /**
     * Application update exception
     * @param message Message
     */
    constructor(message: String) : this(message, null)

    companion object {
        /**
         * Update type not allowed
         */
        const val ERROR_UPDATE_TYPE_NOT_ALLOWED = "Requested update type not allowed for this update"

        /**
         * An update check has failed
         */
        const val ERROR_UPDATE_CHECK_FAILED = "Error checking application update"

        /**
         * An update activity returned unknown update result
         */
        const val ERROR_UNKNOWN_UPDATE_RESULT = "Unknown update result"

        /**
         * An update has failed
         */
        const val ERROR_UPDATE_FAILED = "Update failed"

        /**
         * Immediate update was requested but no update available
         */
        const val ERROR_NO_IMMEDIATE_UPDATE = "No update available this time. Please try again later"
    }
}