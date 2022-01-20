package ro.sts.dgc.rules.data.rules

interface RulesRepository : RulesDataSource {
    suspend fun loadRules(rulesUrl: String)
}