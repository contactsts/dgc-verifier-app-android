package ro.sts.dgc.rules.data.source.local.rules

import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.RuleIdentifier
import ro.sts.dgc.rules.data.Type
import java.time.ZonedDateTime

class DefaultRulesLocalDataSource(private val rulesDao: RulesDao) : RulesLocalDataSource {

    override fun addRules(ruleIdentifiers: Collection<RuleIdentifier>, rules: Collection<Rule>) {
        rulesDao.insertRulesData(
            ruleIdentifiers.map { it.toRuleIdentifierLocal() },
            rules.map { it.toRuleWithDescriptionLocal() }
        )
    }

    override fun removeRulesBy(identifiers: Collection<String>) {
        rulesDao.deleteRulesDataBy(identifiers)
    }

    override fun getRuleIdentifiers(): List<RuleIdentifier> =
        rulesDao.getRuleIdentifiers().map { it.toRuleIdentifier() }

    override fun getRulesBy(
        countryIsoCode: String,
        validationClock: ZonedDateTime,
        type: Type,
        ruleCertificateType: RuleCertificateType
    ): List<Rule> = rulesDao.getRulesWithDescriptionsBy(
        countryIsoCode,
        validationClock,
        type,
        ruleCertificateType,
        RuleCertificateType.GENERAL
    ).toRules()

    override fun getRulesBy(
        countryIsoCode: String
    ): List<Rule> = rulesDao.getRulesWithDescriptionsBy(
        countryIsoCode
    ).toRules()
}