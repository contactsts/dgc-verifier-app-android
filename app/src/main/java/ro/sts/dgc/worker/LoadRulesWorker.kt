package ro.sts.dgc.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ro.sts.dgc.pinning.ConfigRepository
import ro.sts.dgc.rules.data.rules.RulesRepository
import timber.log.Timber

@HiltWorker
class LoadRulesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
    private val configRepository: ConfigRepository,
    private val rulesRepository: RulesRepository
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        Timber.d("Rules loading start")
        return try {
            val config = configRepository.local().getConfig()
            rulesRepository.loadRules(config.getRulesUrl())
            Timber.d("Rules loading succeeded")
            Result.success()
        } catch (error: Throwable) {
            Timber.d(error, "Rules Loading Error: $error")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "LoadRulesWorker"
    }
}