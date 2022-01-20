package ro.sts.dgc

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import ro.sts.dgc.data.Preferences
import ro.sts.dgc.data.PreferencesImpl
import ro.sts.dgc.worker.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class DgcApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var preferences: Preferences

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        EmojiCompat.init(
            FontRequestEmojiCompatConfig(
                applicationContext,
                FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs
                )
            ).apply {
                setReplaceAll(true)
            }
        )

        WorkManager.getInstance(this).apply {
            scheduleUniquePeriodicWorker<LoadConfigsWorker>(LoadConfigsWorker.WORK_NAME, 1, TimeUnit.HOURS)
            scheduleUniquePeriodicWorker<LoadRulesWorker>(LoadRulesWorker.WORK_NAME, 1, TimeUnit.HOURS)
            scheduleUniquePeriodicWorker<LoadNationalRulesWorker>(LoadNationalRulesWorker.WORK_NAME, 1, TimeUnit.HOURS)
            scheduleUniquePeriodicWorker<LoadCertificatesWorker>(LoadCertificatesWorker.WORK_NAME, 1, TimeUnit.HOURS)
            scheduleUniquePeriodicWorker<LoadCountriesWorker>(LoadCountriesWorker.WORK_NAME, 1, TimeUnit.HOURS)
            scheduleUniquePeriodicWorker<LoadValueSetsWorker>(LoadValueSetsWorker.WORK_NAME, 1, TimeUnit.HOURS)
            updateWorkerVersion()
        }

        Timber.i("DGC Verifier version ${BuildConfig.VERSION_NAME} is starting")
    }

    private fun periodicWorkPolicy(): ExistingPeriodicWorkPolicy {
        return if (preferences.workerVersion < PreferencesImpl.CURRENT_WORKER_VERSION) {
            ExistingPeriodicWorkPolicy.REPLACE
        } else {
            ExistingPeriodicWorkPolicy.KEEP
        }
    }

    private fun updateWorkerVersion() {
        preferences.workerVersion = PreferencesImpl.CURRENT_WORKER_VERSION
    }

    private inline fun <reified T : ListenableWorker> WorkManager.scheduleUniquePeriodicWorker(uniqueWorkName: String, repeatInterval: Long, repeatIntervalTimeUnit: TimeUnit) =
        this.enqueueUniquePeriodicWork(
            uniqueWorkName,
            periodicWorkPolicy(),
            PeriodicWorkRequestBuilder<T>(repeatInterval, repeatIntervalTimeUnit)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MAX_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        )
}