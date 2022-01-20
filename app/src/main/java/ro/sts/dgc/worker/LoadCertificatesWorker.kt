package ro.sts.dgc.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ro.sts.dgc.data.CertificateRepository
import ro.sts.dgc.pinning.ConfigRepository
import timber.log.Timber

@HiltWorker
class LoadCertificatesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val configRepository: ConfigRepository,
    private val certificateRepository: CertificateRepository
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        Timber.d("Certificate fetching start")
        val config = configRepository.local().getConfig()
        val res = certificateRepository.fetchCertificates(config.getCertStatusUrl(), config.getCertUpdateUrl())
        Timber.d("Certificate fetching result: ${res == true}")
        return if (res == true) Result.success() else Result.retry()
    }

    companion object {
        const val WORK_NAME = "LoadCertificatesWorker"
    }
}