package ro.sts.dgc

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


const val YEAR_MONTH_DAY = "yyyy-MM-dd"
const val YEAR_MONTH = "yyyy-MM"

const val FORMATTED_YEAR_MONTH_DAY = "MMM d, yyyy"
const val FORMATTED_YEAR_MONTH = "MMM, yyyy"

private const val FORMATTED_DATE_TIME = "MMM d, yyyy, HH:mm"

private fun String.toZonedDateTime(): ZonedDateTime? = try {
    ZonedDateTime.parse(this)
} catch (error: Throwable) {
    null
}

private fun String.toLocalDateTime(): LocalDateTime? = try {
    LocalDateTime.parse(this)
} catch (error: Throwable) {
    null
}

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMATTED_DATE_TIME)

fun String.toFormattedDateTime(): String? =
    this.toZonedDateTime()?.let { "${DATE_TIME_FORMATTER.format(it)} (UTC)" }
        ?: this.toLocalDateTime()?.let { "${DATE_TIME_FORMATTER.format(it)} (UTC)" }

fun String.parseFromTo(from: String, to: String): String? {
    return try {
        val parser = SimpleDateFormat(from, Locale.US)
        val formatter = SimpleDateFormat(to, Locale.US)
        return formatter.format(parser.parse(this)!!)
    } catch (ex: Exception) {
        null
    }
}

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

fun LocalDateTime.formatWith(pattern: String): String = DateTimeFormatter.ofPattern(pattern).format(this)