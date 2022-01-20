package ro.sts.dgc.rules

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.BooleanNode
import ro.sts.dgc.certlogic.evaluate

class DefaultJsonLogicValidator : JsonLogicValidator {
    override fun isDataValid(rule: JsonNode, data: JsonNode): Boolean =
        (evaluate(rule, data) as BooleanNode).asBoolean()
}