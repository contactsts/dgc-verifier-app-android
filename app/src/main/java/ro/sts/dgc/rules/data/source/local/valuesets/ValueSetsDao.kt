package ro.sts.dgc.rules.data.source.local.valuesets

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class ValueSetsDao {
    @Query("SELECT * from valuesets")
    abstract fun getAll(): List<ValueSetLocal>

    @Query("SELECT * from value_set_identifier")
    abstract fun getAllIdentifiers(): List<ValueSetIdentifierLocal>

    @Insert
    abstract fun insert(vararg valueSetsLocal: ValueSetLocal)

    @Query("DELETE FROM valuesets")
    abstract fun deleteAll()

    @Query("DELETE FROM valuesets WHERE valueSetId IN (:setIds)")
    abstract fun deleteValueSetsBy(setIds: List<String>)

    @Query("DELETE FROM value_set_identifier WHERE valueSetIdentifierId IN (:setIds)")
    abstract fun deleteValueSetIdentifiersBy(setIds: List<String>)

    @Transaction
    open fun deleteSetsBy(setIds: List<String>) {
        deleteValueSetsBy(setIds)
        deleteValueSetIdentifiersBy(setIds)
    }

    @Transaction
    open fun insertSets(
        valueSetsIdentifiersLocal: Array<ValueSetIdentifierLocal>,
        valueSetsLocal: Array<ValueSetLocal>
    ) {
        insertIdentifiers(*valueSetsIdentifiersLocal)
        insertValueSets(*valueSetsLocal)
    }

    @Insert
    abstract fun insertIdentifiers(vararg valueSetsIdentifiersLocal: ValueSetIdentifierLocal)

    @Insert
    abstract fun insertValueSets(vararg valueSetsLocal: ValueSetLocal)
}