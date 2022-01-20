package ro.sts.dgc.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Preferences {

    var resumeToken: Long

    var lastKeysSyncTimeMillis: Long

    var selectedCountryIsoCode: String?

    var useNationalRules: Boolean

    var userLanguage: String?

    var workerVersion: Long

    fun clear()
}

/**
 * [Preferences] impl backed by [android.content.SharedPreferences].
 */
class PreferencesImpl(context: Context) : Preferences {

    private var preferences: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)
    }

    override var resumeToken by LongPreference(preferences, KEY_RESUME_TOKEN, -1)
    override var lastKeysSyncTimeMillis by LongPreference(preferences, KEY_LAST_KEYS_SYNC_TIME_MILLIS, -1)
    override var selectedCountryIsoCode: String? by StringPreference(preferences, KEY_SELECTED_COUNTRY_ISO_CODE, DEFAULT_COUNTRY_ISO_CODE)
    override var useNationalRules: Boolean by BooleanPreference(preferences, KEY_USE_NATIONAL_RULES, DEFAULT_USE_NATIONAL_RULES)
    override var userLanguage: String? by StringPreference(preferences, KEY_USER_LANGUAGE, null)
    override var workerVersion by LongPreference(preferences, KEY_WORKER_VERSION, 1)

    override fun clear() {
        preferences.value.edit().clear().apply()
    }

    companion object {
        private const val USER_PREF = "ro.sts.dgc.preferences"
        private const val KEY_RESUME_TOKEN = "resume_token"
        private const val KEY_LAST_KEYS_SYNC_TIME_MILLIS = "last_keys_sync_time_millis"
        private const val KEY_SELECTED_COUNTRY_ISO_CODE = "selected_country_iso_code"
        private const val KEY_USE_NATIONAL_RULES = "use_national_rules"
        private const val KEY_USER_LANGUAGE = "user_language"
        private const val KEY_WORKER_VERSION = "worker_version"

        const val DEFAULT_COUNTRY_ISO_CODE = "ro"
        const val DEFAULT_USE_NATIONAL_RULES = true
        const val CURRENT_WORKER_VERSION = 2L
    }
}

class LongPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit { putLong(name, value) }
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String? = null
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}