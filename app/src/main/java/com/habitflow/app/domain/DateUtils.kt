package com.habitflow.app.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object DateUtils {
    private val fmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD

    private val DAY_LABELS = listOf("Pon", "Uto", "Sre", "Čet", "Pet", "Sub", "Ned") // ponedeljak-prvi
    private val MONTH_GENITIVE = listOf(
        "jan", "feb", "mart", "apr", "maj", "jun", "jul", "avg", "sep", "okt", "nov", "dec"
    )
    private val MONTH_NAMES = listOf(
        "Januar", "Februar", "Mart", "April", "Maj", "Jun",
        "Jul", "Avgust", "Septembar", "Oktobar", "Novembar", "Decembar"
    )

    fun today(): String = LocalDate.now().format(fmt)

    fun format(date: LocalDate): String = date.format(fmt)

    fun parse(date: String): LocalDate = LocalDate.parse(date, fmt)

    fun daysAgo(n: Long): String = LocalDate.now().minusDays(n).format(fmt)

    /** Skraćeno ime dana u nedelji, ponedeljak-prvi (npr. "Pon"). */
    fun shortDayName(date: LocalDate): String = DAY_LABELS[date.dayOfWeek.value - 1]

    /** Ponedeljak nedelje kojoj pripada dati datum. */
    fun mondayOf(date: LocalDate): LocalDate = date.with(DayOfWeek.MONDAY)

    /** npr. "24. jul" — za naslov sekcije kad izabrani dan nije danas. */
    fun displayLabel(date: LocalDate): String =
        "${date.dayOfMonth}. ${MONTH_GENITIVE[date.monthValue - 1]}"

    /** npr. "Avgust 2026" — za naslov meseca na Kalendar ekranu. */
    fun monthYearLabel(month: YearMonth): String =
        "${MONTH_NAMES[month.monthValue - 1]} ${month.year}"
}
