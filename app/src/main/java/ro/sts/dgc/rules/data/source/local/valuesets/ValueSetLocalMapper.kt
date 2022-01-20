package ro.sts.dgc.rules.data.source.local.valuesets

import ro.sts.dgc.rules.data.ValueSet
import ro.sts.dgc.rules.data.ValueSetIdentifier

fun ValueSet.toValueSetLocal(): ValueSetLocal = ValueSetLocal(
    valueSetId = this.valueSetId,
    valueSetDate = this.valueSetDate,
    valueSetValues = this.valueSetValues
)


fun ValueSetLocal.toValueSet(): ValueSet = ValueSet(
    valueSetId = this.valueSetId,
    valueSetDate = this.valueSetDate,
    valueSetValues = this.valueSetValues
)

fun ValueSetIdentifier.toValueSetIdentifierLocal(): ValueSetIdentifierLocal = ValueSetIdentifierLocal(
    valueSetIdentifierId = id,
    valueSetHash = hash
)

fun ValueSetIdentifierLocal.toValueSetIdentifier(): ValueSetIdentifier = ValueSetIdentifier(
    id = valueSetIdentifierId,
    hash = valueSetHash
)