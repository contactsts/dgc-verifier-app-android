package ro.sts.dgc.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseRepository {

    suspend fun <P> execute(doOnAsyncBlock: suspend () -> P): P? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                Timber.d("Do network coroutine work")
                doOnAsyncBlock.invoke()
            } catch (e: UnknownHostException) {
                Timber.w(e, "UnknownHostException")
                null
            } catch (e: SocketTimeoutException) {
                Timber.w(e, "SocketTimeoutException")
                null
            } catch (throwable: Throwable) {
                Timber.w(throwable, "Throwable")
                null
            }
        }
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun ResponseBody.stringSuspending() = withContext(Dispatchers.IO) { string() }