package ro.sts.dgc.rules.data.valuesets

import ro.sts.dgc.rules.data.ValueSet
import ro.sts.dgc.rules.data.ValueSetIdentifier
import ro.sts.dgc.rules.data.source.local.valuesets.ValueSetsLocalDataSource
import ro.sts.dgc.rules.data.source.remote.valuesets.ValueSetsRemoteDataSource
import ro.sts.dgc.rules.data.source.remote.valuesets.toValueSet
import ro.sts.dgc.rules.data.source.remote.valuesets.toValueSetIdentifier

class DefaultValueSetsRepository(
    private val remoteDataSource: ValueSetsRemoteDataSource,
    private val localDataSource: ValueSetsLocalDataSource
) : ValueSetsRepository {
    override suspend fun preLoad(url: String) {
        val valueSetsIdentifiersRemote = remoteDataSource.getValueSetsIdentifiers(url).map { it.toValueSetIdentifier() }
        val valueSetsIdentifiersLocal = localDataSource.getValueSetIdentifiers()

        val added = valueSetsIdentifiersRemote - valueSetsIdentifiersLocal
        val removed = valueSetsIdentifiersLocal - valueSetsIdentifiersRemote

        localDataSource.removeValueSetsBy(removed.map { it.id })

        val valueSets = mutableListOf<ValueSet>()
        added.forEach {
            val valueSetRemote = remoteDataSource.getValueSet("$url/${it.hash}")
            if (valueSetRemote != null) {
                valueSets.add(valueSetRemote.toValueSet())
            }
        }

        if (valueSets.isNotEmpty()) {
            localDataSource.addValueSets(added, valueSets)
        }
    }

    override suspend fun getValueSets(): List<ValueSet> = localDataSource.getValueSets()

    override suspend fun getValueSetIdentifiers(): List<ValueSetIdentifier> = localDataSource.getValueSetIdentifiers()
}