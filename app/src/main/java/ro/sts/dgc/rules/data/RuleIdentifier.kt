package ro.sts.dgc.rules.data

data class RuleIdentifier(
    val identifier: String,
    val version: String,
    val country: String,
    val hash: String
)