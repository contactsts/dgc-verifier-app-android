package ro.sts.dgc.rules.data.source.remote.valuesets

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

data class ValueSetRemote(
    @JsonProperty("valueSetId")
    val valueSetId: String,
    @JsonProperty("valueSetDate")
    val valueSetDate: LocalDate,
    @JsonProperty("valueSetValues")
    val valueSetValues: JsonNode
)