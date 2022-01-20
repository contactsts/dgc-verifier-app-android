package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.Type
import java.time.ZonedDateTime

@Dao
abstract class RulesDao {
    @Query("SELECT * from rules")
    abstract fun getAll(): List<RuleLocal>

    @Query("SELECT * from descriptions")
    abstract fun getDescriptionAll(): List<DescriptionLocal>

    @Transaction
    @Query("SELECT * FROM rules WHERE :countryIsoCode = countryCode AND (:validationClock BETWEEN validFrom AND validTo) AND :type = type AND (:ruleCertificateType = ruleCertificateType OR :generalRuleCertificateType = ruleCertificateType)")
    abstract fun getRulesWithDescriptionsBy(
        countryIsoCode: String,
        validationClock: ZonedDateTime,
        type: Type,
        ruleCertificateType: RuleCertificateType,
        generalRuleCertificateType: RuleCertificateType
    ): List<RuleWithDescriptionsLocal>

    @Transaction
    @Query("SELECT * FROM rules WHERE :countryIsoCode = countryCode")
    abstract fun getRulesWithDescriptionsBy(countryIsoCode: String): List<RuleWithDescriptionsLocal>

    @Insert
    abstract fun insertRule(rule: RuleLocal): Long

    @Query("DELETE FROM rules WHERE identifier IN (:identifiers)")
    abstract fun deleteRulesBy(identifiers: Collection<String>)

    @Insert
    abstract fun insertDescriptions(vararg descriptions: DescriptionLocal)

    @Insert
    abstract fun insertRuleIdentifiers(ruleIdentifiers: Collection<RuleIdentifierLocal>)

    fun insertAll(rulesWithDescription: Collection<RuleWithDescriptionsLocal>) {
        rulesWithDescription.forEach { ruleWithDescriptionsLocal ->
            val rule = ruleWithDescriptionsLocal.rule
            val descriptions = ruleWithDescriptionsLocal.descriptions
            val ruleId = insertRule(rule)
            val descriptionsToBeInserted = mutableListOf<DescriptionLocal>()
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

    @Query("DELETE FROM rules WHERE identifier NOT IN (:identifiers)")
    abstract fun deleteAllExcept(identifiers: Array<String>)

    @Query("DELETE FROM rule_identifiers WHERE identifier IN (:identifiers)")
    abstract fun deleteRuleIdentifiersBy(identifiers: Collection<String>)

    @Transaction
    open fun deleteRulesDataBy(identifiers: Collection<String>) {
        identifiers.chunked(100).forEach {
            deleteRulesBy(it)
            deleteRuleIdentifiersBy(it)
        }
    }

    @Transaction
    open fun insertRulesData(ruleIdentifiers: Collection<RuleIdentifierLocal>, rulesWithDescription: Collection<RuleWithDescriptionsLocal>) {
        insertRuleIdentifiers(ruleIdentifiers)
        insertAll(rulesWithDescription)
    }

    @Query("SELECT * from rule_identifiers")
    abstract fun getRuleIdentifiers(): List<RuleIdentifierLocal>
}