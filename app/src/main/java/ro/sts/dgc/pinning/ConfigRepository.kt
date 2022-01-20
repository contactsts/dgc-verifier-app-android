package ro.sts.dgc.pinning

interface ConfigRepository : ConfigDataSource {

    /**
     * Provides specifically local data source version.
     */
    fun local(): ConfigDataSource
}