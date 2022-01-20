package ro.sts.dgc.rules.data.source.local.valuesets

import ro.sts.dgc.rules.data.ValueSet
import ro.sts.dgc.rules.data.ValueSetIdentifier

class DefaultValueSetsLocalDataSource(private val dao: ValueSetsDao) : ValueSetsLocalDataSource {

    override suspend fun updateValueSets(valueSets: List<ValueSet>) {
        dao.apply {
            deleteAll()
            insert(*valueSets.map { it.toValueSetLocal() }.toTypedArray())
        }
    }

    override suspend fun addValueSets(valueSetIdentifiers: List<ValueSetIdentifier>, valueSets: List<ValueSet>) {
        dao.insertSets(
            *valueSetIdentifiers.map { it.toValueSetIdentifierLocal() }.toTypedArray(),
            *valueSets.map { it.toValueSetLocal() }.toTypedArray()
        )
    }

    override suspend fun removeValueSetsBy(setIds: List<String>) {
        dao.deleteSetsBy(setIds)
    }

    override suspend fun getValueSets(): List<ValueSet> = dao.getAll().map { it.toValueSet() }

    override suspend fun getValueSetIdentifiers(): List<ValueSetIdentifier> =
        dao.getAllIdentifiers().map { it.toValueSetIdentifier() }
}