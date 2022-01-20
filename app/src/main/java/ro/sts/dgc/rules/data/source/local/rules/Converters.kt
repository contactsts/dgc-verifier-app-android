package ro.sts.dgc.rules.data.source.local.rules

import androidx.room.TypeConverter
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ro.sts.dgc.rules.UTC_ZONE_ID
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class Converters {
    @TypeConverter
    fun timestampToLocalDate(value: Long?): LocalDate = if (value != null) {
        val instant: Instant = Instant.ofEpochMilli(value)
        ZonedDateTime.ofInstant(instant, UTC_ZONE_ID)
    } else {
        ZonedDateTime.now(UTC_ZONE_ID)
    }.toLocalDate()

    @TypeConverter
    fun localDateToTimestamp(localDate: LocalDate?): Long {
        return (localDate?.atStartOfDay(UTC_ZONE_ID)
            ?: ZonedDateTime.now(UTC_ZONE_ID)).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): ZonedDateTime = if (value != null) {
        val instant: Instant = Instant.EPOCH.plus(value, ChronoUnit.MICROS)
        ZonedDateTime.ofInstant(instant, UTC_ZONE_ID)
    } else {
        ZonedDateTime.now(UTC_ZONE_ID)
    }

    @TypeConverter
    fun zonedDateTimeToTimestamp(zonedDateTime: ZonedDateTime?): Long {
        return ChronoUnit.MICROS.between(
            Instant.EPOCH,
            (zonedDateTime?.withZoneSameInstant(UTC_ZONE_ID)
                ?: ZonedDateTime.now(UTC_ZONE_ID)).toInstant()
        )
    }

    @TypeConverter
    fun fromString(value: String?): List<String> {
        val objectMap = ObjectMapper()
        return objectMap.readValue(value, Array<String>::class.java).toList()
    }

    @TypeConverter
    fun fromList(list: List<String?>?): String {
        val objectMapper = ObjectMapper()
        return objectMapper.writeValueAsString(list ?: emptyList<String>())
    }

    @TypeConverter
    fun fromJsonNode(value: JsonNode?): String {
        val objectMap = ObjectMapper()
        return objectMap.writeValueAsString(value ?: objectMap.createObjectNode())
    }

    @TypeConverter
    fun toJsonNodeList(value: String?): JsonNode {
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(value ?: "")
    }
}