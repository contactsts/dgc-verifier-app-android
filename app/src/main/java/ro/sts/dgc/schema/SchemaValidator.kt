package ro.sts.dgc.schema

import ro.sts.dgc.model.VerificationResult

interface SchemaValidator {

    fun validate(cbor: ByteArray, verificationResult: VerificationResult): Boolean
}