package com.example.dlinkexam.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * The API returns naive local-time strings ("yyyy-MM-dd HH:mm:ss") with no offset,
 * already in Taiwan local time — so formatting is a straight reformat, not a zone conversion.
 */
object TaipeiTimeFormatter {
    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val displayFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm")

    fun format(raw: String): String =
        runCatching { LocalDateTime.parse(raw, inputFormatter).format(displayFormatter) }
            .getOrDefault(raw)
}
