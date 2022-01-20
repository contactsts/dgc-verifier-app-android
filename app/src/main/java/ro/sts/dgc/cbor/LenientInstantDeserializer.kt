package ro.sts.dgc.cbor

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.time.Instant

/**
 * Some countries encode Instants in a wrong format, e.g. missing "Z" or the offset "+0200" instead of "+02:00", so we'll try to work around those issues
 */
class LenientInstantDeserializer : JsonDeserializer<Instant?>() {
    @Throws(IOException::class, JsonProcessingException::class)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant? {
        val value = p.valueAsString
        val fixOffset = value.replace(Regex("\\+(\\d{2})(\\d{2})")) { "+${it.groupValues[1]}:${it.groupValues[2]}" }
        val fixZulu = if (fixOffset.contains('Z') || fixOffset.contains("+")) fixOffset else fixOffset + 'Z'
        return Instant.parse(fixZulu)
    }
}