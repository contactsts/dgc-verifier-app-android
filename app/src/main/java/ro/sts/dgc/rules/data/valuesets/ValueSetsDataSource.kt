package ro.sts.dgc.rules.data.valuesets

import ro.sts.dgc.rules.data.ValueSet
import ro.sts.dgc.rules.data.ValueSetIdentifier

interface ValueSetsDataSource {
    /**
     * Provides list of value sets.
     */
    suspend fun getValueSets(): List<ValueSet>

    suspend fun getValueSetIdentifiers(): List<ValueSetIdentifier>
}