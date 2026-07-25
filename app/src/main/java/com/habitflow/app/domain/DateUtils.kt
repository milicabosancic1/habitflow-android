package com.habitflow.app.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val fmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    fun today(): String = LocalDate.now().format(fmt)

    fun format(date: LocalDate): String = date.format(fmt)

    fun parse(date: String): LocalDate = LocalDate.parse(date, fmt)

    fun daysAgo(n: Long): String = LocalDate.now().minusDays(n).format(fmt)
}
