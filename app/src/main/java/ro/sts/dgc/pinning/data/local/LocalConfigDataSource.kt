package ro.sts.dgc.pinning.data.local

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import ro.sts.dgc.BuildConfig
import ro.sts.dgc.pinning.Config
import timber.log.Timber
import java.io.*
import javax.inject.Inject

class LocalConfigDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val objectMapper: ObjectMapper
) : MutableConfigDataSource {

    private lateinit var config: Config

    companion object {
        const val DEFAULT_CONFIG_FILE = "local-context-" + BuildConfig.FLAVOR + ".json"
        const val CONFIG_FILE = "config.json"
    }

    override fun setConfig(config: Config): Config {
        this.config = config
        return saveConfig(this.config)
    }

    override fun getConfig(): Config {
        if (!this::config.isInitialized) {
            try {
                config = loadConfig()
            } catch (error: Throwable) {
                Timber.v("Error loading config from local.")
            }
            if (!this::config.isInitialized) {
                config = defaultConfig()
            }
        }
        return config
    }

    private fun configFile(): File = File(context.filesDir, CONFIG_FILE)

    private fun loadConfig(): Config =
        BufferedReader(InputStreamReader(FileInputStream(configFile()))).use {
            objectMapper.readValue(it.readText(), Config::class.java)
        }

    private fun saveConfig(config: Config): Config {
        FileWriter(configFile()).use {
            objectMapper.writeValue(it, config)
        }
        return config
    }

    private fun defaultConfig(): Config =
        context.assets.open(DEFAULT_CONFIG_FILE).bufferedReader().use {
            objectMapper.readValue(it.readText(), Config::class.java)
        }
}