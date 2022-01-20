package ro.sts.dgc

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.text.Normalizer
import java.util.*

fun ByteArray.asBase64() = Base64.getEncoder().encodeToString(this)

fun ByteArray.asBase64Url() = Base64.getUrlEncoder().encodeToString(this)

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun String.fromBase64() = Base64.getDecoder().decode(this)

fun String.fromBase64Url() = Base64.getUrlDecoder().decode(this)

fun String.fromHexString() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

fun String.base64ToX509Certificate(): X509Certificate? {
    val decoded = android.util.Base64.decode(this, android.util.Base64.NO_WRAP)
    val inputStream = ByteArrayInputStream(decoded)

    return CertificateFactory.getInstance("X.509").generateCertificate(inputStream) as? X509Certificate
}

/**
 * Removes all previously registered observers for current lifecycle owner and live data
 * Callback returns null values if it was passed to live data
 */
fun <T> LiveData<T>.listen(lifecycleOwner: LifecycleOwner, observer: (T) -> (Unit)) {
    removeObservers(lifecycleOwner)
    observe(lifecycleOwner, Observer {
        observer.invoke(it)
    })
}

internal fun CharSequence.stripAccents(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
}

fun Fragment.navigateSafe(directions: NavDirections) {
    try {
        findNavController().navigate(directions)
    } catch (e: IllegalArgumentException) {
        Timber.e(e, "Failed to navigate")
    } catch (e: IllegalStateException) {
        Timber.e(e, "Failed to navigate")
    }
}