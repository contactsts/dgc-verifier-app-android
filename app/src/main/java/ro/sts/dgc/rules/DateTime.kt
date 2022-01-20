package ro.sts.dgc.rules

import java.time.ZoneId
import java.time.ZoneOffset

val UTC_ZONE_ID: ZoneId = ZoneId.ofOffset("", ZoneOffset.UTC).normalized()