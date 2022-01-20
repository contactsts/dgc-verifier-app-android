package ro.sts.dgc.cwt

import ro.sts.dgc.model.VerificationResult
import ro.sts.dgc.ui.model.TestModel
import java.time.Clock
import java.time.Instant
import java.time.LocalDate

/**
 * Decides if the [VerificationResult] was correct, i.e. it can be accepted.
 */
class DecisionService(private val clock: Clock = Clock.systemUTC()) {

    fun decide(verificationResult: VerificationResult): VerificationDecision {
        val decision = VerificationDecision(VerificationDecision.Result.FAIL);

        if (!verificationResult.base45Decoded) {
            decision.result = VerificationDecision.Result.FAIL
            decision.reason = VerificationDecision.Reason.BASE45_DECODE_FAILED
            return decision
        }

        if (!verificationResult.cwtDecoded) {
            decision.result = VerificationDecision.Result.FAIL
            decision.reason = VerificationDecision.Reason.CWT_DECODE_FAILED
            return decision
        }

        if (!verificationResult.cborDecoded) {
            decision.result = VerificationDecision.Result.FAIL
            decision.reason = VerificationDecision.Reason.CBOR_DECODE_FAILED
            return decision
        }

        if (verificationResult.contextIdentifier == null) {
            decision.result = VerificationDecision.Result.FAIL
            decision.reason = VerificationDecision.Reason.CONTEXT_IDENTIFIER_FAILED
            return decision
        }

        if (!verificationResult.coseVerified) {
            decision.result = VerificationDecision.Result.FAIL;
            decision.reason = VerificationDecision.Reason.COSE_VERIFICATION_FAILED

            if (verificationResult.certificateValidFrom == null) {
                decision.reason = VerificationDecision.Reason.COSE_VERIFICATION_FAILED_NO_DSC
            }
            return decision
        }

        verificationResult.issuedAt?.let { issuedAt ->
//            verificationResult.certificateValidFrom?.let { certValidFrom ->
//                if (issuedAt.isBefore(certValidFrom)) {
//                    decision.result = VerificationDecision.Result.FAIL
//                    decision.reason = VerificationDecision.Reason.ISSUED_AT_BEFORE_DSC_VALID_FROM
//                    return decision
//                }
//            }
            if (issuedAt.isAfter(clock.instant())) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.ISSUED_AT_AFTER_NOW
                return decision
            }
        }

        verificationResult.expirationTime?.let { expirationTime ->
//            verificationResult.certificateValidUntil?.let { certValidUntil ->
//                if (expirationTime.isAfter(certValidUntil)) {
//                    decision.result = VerificationDecision.Result.FAIL
//                    decision.reason = VerificationDecision.Reason.EXPIRATION_AT_AFTER_DSC_VALID_UNTIL
//                    return decision
//                }
//            }
            if (expirationTime.isBefore(clock.instant())) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.EXPIRATION_AT_BEFORE_NOW
                return decision
            }
        }

        for (contentType in verificationResult.contentType) {
            if (!verificationResult.certificateValidContent.contains(contentType)) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.CERTIFICATE_ENTRY_TYPE_INVALID
                return decision
            }
        }

        verificationResult.certificate?.vaccinations?.firstOrNull()?.let {
            if (it.date.isAfter(LocalDate.now())) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.CERTIFICATE_VACCINATION_ENTRY_DATE_IN_FUTURE
                return decision
            }
        }

        verificationResult.certificate?.tests?.firstOrNull()?.let {
            if (it.resultPositive.key != TestModel.NOT_DETECTED) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.CERTIFICATE_TEST_ENTRY_RESULT_POSITIVE
                return decision
            }
            if (it.dateTimeSample.isAfter(Instant.now())) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.CERTIFICATE_TEST_ENTRY_TEST_DATE_IN_FUTURE
                return decision
            }
        }

        verificationResult.certificate?.recoveryStatements?.firstOrNull()?.let {
            if (it.certificateValidUntil.isBefore(LocalDate.now())) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.CERTIFICATE_RECOVERY_ENTRY_NOT_VALID_ANYMORE
                return decision
            }
            if (it.certificateValidFrom.isAfter(LocalDate.now())) {
                decision.result = VerificationDecision.Result.FAIL
                decision.reason = VerificationDecision.Reason.CERTIFICATE_RECOVERY_ENTRY_NOT_VALID_SO_FAR
                return decision
            }
        }

        if (verificationResult.rulesValidationFailed) {
            decision.result = VerificationDecision.Result.FAIL
            decision.reason = VerificationDecision.Reason.RULES_VALIDATION_FAILED
            return decision
        }

        decision.result = VerificationDecision.Result.GOOD
        return decision
    }

}