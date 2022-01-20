package ro.sts.dgc.pinning.data.local

import ro.sts.dgc.pinning.Config
import ro.sts.dgc.pinning.ConfigDataSource

interface MutableConfigDataSource : ConfigDataSource {
    fun setConfig(config: Config): Config
}