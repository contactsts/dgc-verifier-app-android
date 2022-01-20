package ro.sts.dgc.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.internal.toLongOrDefault
import ro.sts.dgc.asBase64
import ro.sts.dgc.base64ToX509Certificate
import ro.sts.dgc.data.network.DscApiService
import ro.sts.dgc.data.security.KeyStoreCryptor
import ro.sts.dgc.model.TrustedCertificate
import timber.log.Timber
import java.net.HttpURLConnection
import java.security.MessageDigest
import javax.inject.Inject

class TrustListCertificateRepository @Inject constructor(
    private val dscApiService: DscApiService,
    private val preferences: Preferences,
    private val db: DgcDatabase,
    private val keyStoreCryptor: KeyStoreCryptor
) : BaseRepository(), CertificateRepository {

    private val mutex = Mutex()
    private val validCertList = mutableListOf<String>()
    private val lastSyncLiveData: MutableLiveData<Long> = MutableLiveData(preferences.lastKeysSyncTimeMillis)

    override suspend fun fetchCertificates(statusUrl: String, updateUrl: String): Boolean? {
        mutex.withLock {
            return execute {
                val response = dscApiService.getCertStatus(statusUrl)
                val body = response.body() ?: return@execute false
                validCertList.clear()
                validCertList.addAll(body)

                val resumeToken = preferences.resumeToken
                fetchCertificate(updateUrl, resumeToken)
                db.dscCertificateDao().deleteAllExcept(validCertList.toTypedArray())

                preferences.lastKeysSyncTimeMillis = System.currentTimeMillis()
                lastSyncLiveData.postValue(preferences.lastKeysSyncTimeMillis)
                return@execute true
            }
        }
    }

    override fun getCertificatesBy(kid: String): List<TrustedCertificate> {
        return db.dscCertificateDao().getAllByKid(kid).map {
            TrustedCertificate.fromCert(keyStoreCryptor.decrypt(it.key)?.base64ToX509Certificate()!!)
        }
    }

    override fun getLastSyncTimeMillis(): LiveData<Long> = lastSyncLiveData

    private suspend fun fetchCertificate(url: String, resumeToken: Long) {
        val tokenFormatted = if (resumeToken == -1L) "" else resumeToken.toString()
        val response = dscApiService.getCertUpdate(tokenFormatted, url)

        if (response.isSuccessful && response.code() == HttpURLConnection.HTTP_OK) {
            val headers = response.headers()
            val responseKid = headers[HEADER_KID]
            val responseKidLocked = headers[HEADER_KID_LOCKED]
            val newResumeToken = headers[HEADER_RESUME_TOKEN]
            val responseStr = response.body()?.stringSuspending() ?: return

            if (validCertList.contains(responseKid) && isKidValid(responseKid, responseStr)) {
                Timber.d("DSC Certificate KID: $responseKid verified, new resume token: $newResumeToken")
                val key = DscCertificate(kid = responseKid!!, key = keyStoreCryptor.encrypt(responseStr)!!, kidLocked = responseKidLocked)
                db.dscCertificateDao().insert(key)

                preferences.resumeToken = if (newResumeToken.isNullOrBlank()) resumeToken else newResumeToken.toLongOrDefault(resumeToken)

                newResumeToken?.let {
                    val newToken = it.toLong()
                    fetchCertificate(url, newToken)
                }
            }
        }
    }

    private fun isKidValid(responseKid: String?, responseStr: String): Boolean {
        if (responseKid == null) return false

        val cert = responseStr.base64ToX509Certificate() ?: return false
        val certKid = MessageDigest.getInstance("SHA-256")
            .digest(cert.encoded)
            .copyOf(8)
            .asBase64()

        return responseKid == certKid
    }

    companion object {
        const val HEADER_KID = "x-kid"
        const val HEADER_KID_LOCKED = "x-kid-locked"
        const val HEADER_RESUME_TOKEN = "x-resume-token"
    }
}