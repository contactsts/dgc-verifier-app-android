package ro.sts.dgc.ui.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ro.sts.dgc.R
import ro.sts.dgc.data.PreferencesImpl
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.inject.Inject

class PrivacyPolicyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PrivacyPolicyRepository {

    val remoteVersion = PrivacyPolicyRepository.TERMS_URL

    var localVersionEn = R.raw.privacy_policy_en
    var localVersionRo = R.raw.privacy_policy_ro

    override fun getTermsAndConditions(): Flow<String?> = flow {
        (fetchPrivacyPolicy(remoteVersion) ?: getEmbeddedPrivacyPolicy()).let { text ->
            emit(text)
        }
    }

    private fun fetchPrivacyPolicy(url: String): String? {
        return try {
            val stream = URL(url).openStream()
            return BufferedReader(InputStreamReader(stream)).readText()
        } catch (ex: IOException) {
            Timber.w(ex, "Failed to load remote privacy policy")
            null
        }
    }

    private fun getEmbeddedPrivacyPolicy(): String? = try {
        var version = localVersionEn
        when (PreferencesImpl(context).userLanguage) {
            "ro" -> version = localVersionRo
            "en" -> version = localVersionEn
        }
        context.resources.openRawResource(version).bufferedReader().use { it.readText() }
    } catch (ex: IOException) {
        Timber.w(ex, "Failed to load embedded privacy policy")
        null
    }
}