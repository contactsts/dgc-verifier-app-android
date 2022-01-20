package ro.sts.dgc.rules.data.rules

interface NationalRulesRepository : NationalRulesDataSource {
    suspend fun loadRules(rulesUrl: String)
}