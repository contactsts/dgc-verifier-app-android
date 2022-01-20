package ro.sts.dgc.rules.data.source.local.rules

import ro.sts.dgc.rules.data.Rule
import ro.sts.dgc.rules.data.RuleCertificateType
import ro.sts.dgc.rules.data.RuleIdentifier
import ro.sts.dgc.rules.data.Type
import java.time.ZonedDateTime

class DefaultNationalRulesLocalDataSource(private val nationalRulesDao: NationalRulesDao) : NationalRulesLocalDataSource {

    override fun addRules(ruleIdentifiers: Collection<RuleIdentifier>, rules: Collection<Rule>) {
        nationalRulesDao.insertRulesData(
            ruleIdentifiers.map { it.toNationalRuleIdentifierLocal() },
            rules.map { it.toNationalRuleWithDescriptionLocal() }
        )
    }

    override fun removeRulesBy(identifiers: Collection<String>) {
        nationalRulesDao.deleteRulesDataBy(identifiers)
    }

    override fun getRuleIdentifiers(): List<RuleIdentifier> =
        nationalRulesDao.getRuleIdentifiers().map { it.toRuleIdentifier() }

    override fun getRulesBy(
        countryIsoCode: String,
        validationClock: ZonedDateTime,
        type: Type,
        ruleCertificateType: RuleCertificateType
    ): List<Rule> = nationalRulesDao.getRulesWithDescriptionsBy(
        countryIsoCode,
        validationClock,
        type,
        ruleCertificateType,
        RuleCertificateType.GENERAL
    ).toNationalRules()

    override fun getRulesBy(
        countryIsoCode: String
    ): List<Rule> = nationalRulesDao.getRulesWithDescriptionsBy(
        countryIsoCode
    ).toNationalRules()
}