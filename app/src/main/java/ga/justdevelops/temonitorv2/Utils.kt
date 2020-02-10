package ga.justdevelops.temonitorv2

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

fun toOffsetDateTime(value: String) = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value, OffsetDateTime::from)

fun fromOffsetDateTime(date: OffsetDateTime) = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)