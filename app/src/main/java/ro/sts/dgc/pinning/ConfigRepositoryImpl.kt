package ro.sts.dgc.pinning

import ro.sts.dgc.pinning.data.local.MutableConfigDataSource
import ro.sts.dgc.pinning.data.remote.RemoteConfigDataSource
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val localConfigDataSource: MutableConfigDataSource,
    private val remoteConfigDataSource: RemoteConfigDataSource
) : ConfigRepository {

    override fun local(): ConfigDataSource {
        return localConfigDataSource
    }

    override fun getConfig(): Config {
        return remoteConfigDataSource.getConfig(
            localConfigDataSource.getConfig().getContextUrl()
        ).apply {
            localConfigDataSource.setConfig(this)
        }
    }
}