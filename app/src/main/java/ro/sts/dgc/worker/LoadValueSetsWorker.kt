package ro.sts.dgc.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ro.sts.dgc.pinning.ConfigRepository
import ro.sts.dgc.rules.data.valuesets.ValueSetsRepository
import timber.log.Timber

@HiltWorker
class LoadValueSetsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val configRepository: ConfigRepository,
    private val valueSetsRepository: ValueSetsRepository
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        Timber.d("ValueSets loading start")
        return try {
            val config = configRepository.local().getConfig()
            valueSetsRepository.preLoad(config.getValueSetsUrl())
            Timber.d("ValueSets loading succeeded")
            Result.success()
        } catch (error: Throwable) {
            Timber.d(error, "ValueSets Loading Error: $error")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "LoadValueSetsWorker"
    }
}