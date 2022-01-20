package ro.sts.dgc.rules.data.source.remote.valuesets

interface ValueSetsRemoteDataSource {
    suspend fun getValueSetsIdentifiers(url: String): List<ValueSetIdentifierRemote>

    suspend fun getValueSet(url: String): ValueSetRemote?
}