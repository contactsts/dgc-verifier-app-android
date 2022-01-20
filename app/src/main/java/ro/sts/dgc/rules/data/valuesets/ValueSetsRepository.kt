package ro.sts.dgc.rules.data.valuesets

interface ValueSetsRepository : ValueSetsDataSource {
    suspend fun preLoad(url: String)
}