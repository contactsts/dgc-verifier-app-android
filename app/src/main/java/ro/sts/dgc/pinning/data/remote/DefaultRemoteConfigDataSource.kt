package ro.sts.dgc.pinning.data.remote

import ro.sts.dgc.data.network.DscApiService
import ro.sts.dgc.pinning.Config
import javax.inject.Inject

class DefaultRemoteConfigDataSource @Inject constructor(private val dscApiService: DscApiService) : RemoteConfigDataSource {

    override fun getConfig(url: String): Config = dscApiService.context(url).execute().body()!!
}