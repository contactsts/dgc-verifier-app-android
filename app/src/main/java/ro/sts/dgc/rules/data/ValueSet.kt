package ro.sts.dgc.rules.data

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

data class ValueSet(
    val valueSetId: String,
    val valueSetDate: LocalDate,
    val valueSetValues: JsonNode
)