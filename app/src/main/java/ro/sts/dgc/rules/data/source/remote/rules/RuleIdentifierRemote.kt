package ro.sts.dgc.rules.data.source.remote.rules

import com.fasterxml.jackson.annotation.JsonProperty

data class RuleIdentifierRemote(
    @JsonProperty("identifier")
    val identifier: String,
    @JsonProperty("version")
    val version: String,
    @JsonProperty("country")
    val country: String,
    @JsonProperty("hash")
    val hash: String
)