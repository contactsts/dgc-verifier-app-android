package ro.sts.dgc.rules.data.source.remote.rules

import com.fasterxml.jackson.annotation.JsonProperty

data class DescriptionRemote(
    @JsonProperty("lang") val lang: String,
    @JsonProperty("desc") val desc: String
)