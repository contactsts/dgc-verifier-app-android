package ro.sts.dgc.rules.data.source.local.valuesets

import ro.sts.dgc.rules.data.ValueSetIdentifier
import ro.sts.dgc.rules.data.ValueSet
import ro.sts.dgc.rules.data.valuesets.ValueSetsDataSource

interface ValueSetsLocalDataSource : ValueSetsDataSource {

    suspend fun updateValueSets(valueSets: List<ValueSet>)

    suspend fun addValueSets(
        valueSetIdentifiers: List<ValueSetIdentifier>,
        valueSets: List<ValueSet>
    )

    suspend fun removeValueSetsBy(setIds: List<String>)
}