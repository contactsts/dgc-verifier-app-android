package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.Type
import java.time.ZonedDateTime

@Dao
abstract class NationalRulesDao {
    @Query("SELECT * from national_rules")
    abstract fun getAll(): List<NationalRuleLocal>

    @Query("SELECT * from national_descriptions")
    abstract fun getDescriptionAll(): List<NationalDescriptionLocal>

    @Transaction
    @Query("SELECT * FROM national_rules WHERE :countryIsoCode = countryCode AND (:validationClock BETWEEN validFrom AND validTo) AND :type = type AND (:ruleCertificateType = ruleCertificateType OR :generalRuleCertificateType = ruleCertificateType)")
    abstract fun getRulesWithDescriptionsBy(
        countryIsoCode: String,
        validationClock: ZonedDateTime,
        type: Type,
        ruleCertificateType: RuleCertificateType,
        generalRuleCertificateType: RuleCertificateType
    ): List<NationalRuleWithDescriptionsLocal>

    @Transaction
    @Query("SELECT * FROM national_rules WHERE :countryIsoCode = countryCode")
    abstract fun getRulesWithDescriptionsBy(countryIsoCode: String): List<NationalRuleWithDescriptionsLocal>

    @Insert
    abstract fun insertRule(rule: NationalRuleLocal): Long

    @Query("DELETE FROM national_rules WHERE identifier IN (:identifiers)")
    abstract fun deleteRulesBy(identifiers: Collection<String>)

    @Insert
    abstract fun insertDescriptions(vararg descriptions: NationalDescriptionLocal)

    @Insert
    abstract fun insertRuleIdentifiers(ruleIdentifiers: Collection<NationalRuleIdentifierLocal>)

    fun insertAll(rulesWithDescription: Collection<NationalRuleWithDescriptionsLocal>) {
        rulesWithDescription.forEach { ruleWithDescriptionsLocal ->
            val rule = ruleWithDescriptionsLocal.rule
            val descriptions = ruleWithDescriptionsLocal.descriptions
            val ruleId = insertRule(rule)
            val descriptionsToBeInserted = mutableListOf<NationalDescriptionLocal>()
            descriptions.forEach { descriptionLocal ->
                descriptionsToBeInserted.add(
                    descriptionLocal.copy(
                        ruleContainerId = ruleId
                    )
                )
            }
            insertDescriptions(*descriptionsToBeInserted.toTypedArray())
        }
    }

    @Query("DELETE FROM national_rules WHERE identifier NOT IN (:identifiers)")
    abstract fun deleteAllExcept(identifiers: Array<String>)

    @Query("DELETE FROM national_rule_identifiers WHERE identifier IN (:identifiers)")
    abstract fun deleteRuleIdentifiersBy(identifiers: Collection<String>)

    @Transaction
    open fun deleteRulesDataBy(identifiers: Collection<String>) {
        identifiers.chunked(100).forEach {
            deleteRulesBy(it)
            deleteRuleIdentifiersBy(it)
        }
    }

    @Transaction
    open fun insertRulesData(ruleIdentifiers: Collection<NationalRuleIdentifierLocal>, rulesWithDescription: Collection<NationalRuleWithDescriptionsLocal>) {
        insertRuleIdentifiers(ruleIdentifiers)
        insertAll(rulesWithDescription)
    }

    @Query("SELECT * from national_rule_identifiers")
    abstract fun getRuleIdentifiers(): List<NationalRuleIdentifierLocal>
}