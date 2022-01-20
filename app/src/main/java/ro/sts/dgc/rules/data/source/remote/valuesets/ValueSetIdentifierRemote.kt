package ro.sts.dgc.rules.data.source.remote.valuesets

import com.fasterxml.jackson.annotation.JsonProperty

class ValueSetIdentifierRemote(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("hash")
    val hash: String
)