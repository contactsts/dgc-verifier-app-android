package ro.sts.dgc.data

import androidx.lifecycle.LiveData
import ro.sts.dgc.model.TrustedCertificate

interface CertificateRepository {

    suspend fun fetchCertificates(statusUrl: String, updateUrl: String): Boolean?

    fun getCertificatesBy(kid: String): List<TrustedCertificate>

    fun getLastSyncTimeMillis(): LiveData<Long>
}