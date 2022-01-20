package ro.sts.dgc.util

import android.content.Context
import android.content.res.Configuration
import ro.sts.dgc.R
import java.util.*

object LocaleUtil {

    const val DEFAULT_COUNTRY = "US"

    fun isSystemLangNotEnglish(context: Context): Boolean {
        return context.getString(R.string.language_key) != "en"
    }

    fun updateLanguage(context: Context, language: String?): Context? {
        if (!language.isNullOrEmpty()) {
            val config = Configuration()
            config.setLocale(Locale(language, DEFAULT_COUNTRY))
            return context.createConfigurationContext(config)
        }
        return context
    }

}