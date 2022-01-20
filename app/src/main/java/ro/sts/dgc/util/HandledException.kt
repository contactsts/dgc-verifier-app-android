package ro.sts.dgc.util

import android.content.Context
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Error wrapper that allows to handle errors by UI
 */
open class HandledException constructor(val msg: String?, val stringResource: Int?, var titleRes: Int? = null) : Exception() {

    constructor(message: String) : this(message, null)

    constructor(stringResource: Int) : this(null, stringResource)

    open fun getText(context: Context): String = msg ?: context.getString(stringResource!!)

    fun getTitle(context: Context): String? = if (titleRes == null) {
        null
    } else {
        context.getString(titleRes!!)
    }

    companion object {

        fun from(ex: Throwable) = when (ex) {
            is HandledException -> ex
            is SocketTimeoutException -> ConnectionTimeoutException()
            is UnknownHostException, is ConnectException, is SocketException -> NoConnectionException()
            else -> HandledException(ex.message!!)
        }
    }
}