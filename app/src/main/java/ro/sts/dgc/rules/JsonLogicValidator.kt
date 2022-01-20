package ro.sts.dgc.rules

import com.fasterxml.jackson.databind.JsonNode

interface JsonLogicValidator {
    fun isDataValid(rule: JsonNode, data: JsonNode): Boolean
}