package ro.sts.dgc.util

/**
 * An error that signals while no internet connection is available
 */
open class NoConnectionException : HandledException("Unable to connect with server. Please, check your internet connection.")

/**
 * An error that signals that connection timeout exception was received
 */
class ConnectionTimeoutException : NoConnectionException()