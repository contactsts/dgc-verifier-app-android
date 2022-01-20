package ro.sts.dgc.cbor

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.time.LocalDate

/**
 * Some countries include a timestamp, e.g. "T00:00:00", where everybody else expects YYYY-MM-DD only, so we'll strip it, to be able to parse it.
 */
class LenientLocalDateDeserializer : JsonDeserializer<LocalDate?>() {
    @Throws(IOException::class, JsonProcessingException::class)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate? {
        val value = p.valueAsString
        return LocalDate.parse(value.substringBefore("T"))
    }
}