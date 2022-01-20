package ro.sts.dgc.pinning.data.remote

import ro.sts.dgc.pinning.Config

interface RemoteConfigDataSource {

    fun getConfig(url: String): Config
}