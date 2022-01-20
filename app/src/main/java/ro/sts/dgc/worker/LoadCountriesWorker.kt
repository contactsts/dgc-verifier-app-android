package ro.sts.dgc.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ro.sts.dgc.pinning.ConfigRepository
import ro.sts.dgc.rules.data.countries.CountriesRepository
import timber.log.Timber

@HiltWorker
class LoadCountriesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val configRepository: ConfigRepository,
    private val countriesRepository: CountriesRepository
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        Timber.d("Countries loading start")
        return try {
            val config = configRepository.local().getConfig()
            countriesRepository.preLoadCountries(config.getCountriesUrl())
            Timber.d("Countries loading succeeded")
            Result.success()
        } catch (error: Throwable) {
            Timber.d(error, "Countries Loading Error: $error")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "LoadCountriesWorker"
    }
}