package ro.sts.dgc.rules.data.source.remote.valuesets

import ro.sts.dgc.rules.data.ValueSet
import ro.sts.dgc.rules.data.ValueSetIdentifier

fun ValueSetRemote.toValueSet(): ValueSet = ValueSet(
    valueSetId = this.valueSetId,
    valueSetDate = this.valueSetDate,
    valueSetValues = this.valueSetValues
)

fun ValueSetIdentifierRemote.toValueSetIdentifier(): ValueSetIdentifier = ValueSetIdentifier(
    id = id,
    hash = hash
)