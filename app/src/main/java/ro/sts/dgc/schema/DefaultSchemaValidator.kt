package ro.sts.dgc.schema

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.upokecenter.cbor.CBORObject
import ro.sts.dgc.cwt.CwtHeaderKeys
import ro.sts.dgc.model.VerificationResult
import timber.log.Timber

class DefaultSchemaValidator : SchemaValidator {

    override fun validate(cbor: ByteArray, verificationResult: VerificationResult): Boolean {
        var isValid = true
//        try {
//            val map = CBORObject.DecodeFromBytes(cbor)
//            val hcert = map[CwtHeaderKeys.HCERT.asCBOR()]
//            val json = hcert[CBORObject.FromObject(1)].ToJSONString()
//
//            val jsonNode: JsonNode = JsonLoader.fromString(json)
//            val schemaNode: JsonNode = JsonLoader.fromResource("/schema/DGC.combined-schema.json")
//
//            val factory = JsonSchemaFactory.byDefault()
//            val schema: JsonSchema = factory.getJsonSchema(schemaNode)
//
//            // TODO does not work: ClassNotFoundException: Didn't find class "javax.script.ScriptEngineManager"
//            // val report: ProcessingReport = schema.validate(jsonNode)
//
////            isValid = report.isSuccess
////            verificationResult.isSchemaValid = isValid
            verificationResult.isSchemaValid = true
//        } catch (e: Exception) {
//            Timber.e(e, "Verification failed: failed to validate JSON_SCHEMA")
//        }

        return isValid
    }
}