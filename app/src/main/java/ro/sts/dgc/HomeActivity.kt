package ro.sts.dgc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import ro.sts.dgc.data.PreferencesImpl
import ro.sts.dgc.databinding.ActivityHomeBinding
import ro.sts.dgc.ui.AppUpdateReadyDialog
import ro.sts.dgc.update.AppUpdateView
import ro.sts.dgc.update.AppUpdateWrapper
import ro.sts.dgc.update.UpdateFlowBreaker
import ro.sts.dgc.update.startFlexibleUpdate
import ro.sts.dgc.util.LocaleUtil
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), AppUpdateView {

    /**
     * View
     */
    private lateinit var binding: ActivityHomeBinding

    /**
     * Update flow
     */
    private lateinit var updateWrapper: AppUpdateWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Starts flexible update flow
        updateWrapper = startFlexibleUpdate(
            AppUpdateManagerFactory.create(this.applicationContext), this, UpdateFlowBreaker.alwaysOn()
        )
    }

    override fun attachBaseContext(newBase: Context) {
        val language = PreferencesImpl(newBase).userLanguage
        super.attachBaseContext(LocaleUtil.updateLanguage(newBase, language))
    }

    // Passes an activity result to wrapper to check for play-core interaction
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (updateWrapper.checkActivityResult(requestCode, resultCode)) {
            // Result handled and processed
            return
        }
    }

    /**
     * Returns hosting activity for update process
     */
    override val activity: Activity = this

    override fun updateReady() {
        notifyAppUpdateReady()
    }

    override fun updateFailed(e: Throwable) {
        Timber.e(e, "In-App Update failed")
    }

    private fun notifyAppUpdateReady() {
        AppUpdateReadyDialog(this).apply {
            setOnCancelListener {
                updateWrapper.userCanceledUpdate()
            }
            setAppUpdateConfirmClickListener {
                updateWrapper.userConfirmedUpdate()
            }
            setOnDismissListener {
                updateWrapper.userCanceledUpdate()
            }
            show()
        }
    }
}