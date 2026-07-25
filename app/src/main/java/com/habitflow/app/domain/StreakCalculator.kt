package com.habitflow.app.domain

import java.time.LocalDate

/**
 * Računa nizove (streak) na osnovu istorije završenih dana.
 * Čista logika bez zavisnosti — laka za jedinično testiranje i za objašnjavanje
 * na odbrani rada.
 */
object StreakCalculator {

    /**
     * Trenutni niz: broj uzastopnih dana zaključno sa danas (ili juče)
     * u kojima je navika završena (DONE).
     *
     * @param doneDates skup datuma (YYYY-MM-DD) kada je navika bila DONE
     * @param today današnji datum
     */
    fun currentStreak(doneDates: Set<String>, today: LocalDate = LocalDate.now()): Int {
        if (doneDates.isEmpty()) return 0

        // Dozvoli da niz "važi" ako je danas još nije urađena, ali juče jeste.
        var cursor = if (doneDates.contains(today.toString())) today else today.minusDays(1)
        var streak = 0
        while (doneDates.contains(cursor.toString())) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    /** Najduži niz ikada u datoj istoriji. */
    fun longestStreak(doneDates: Set<String>): Int {
        if (doneDates.isEmpty()) return 0
        val sorted = doneDates.map { LocalDate.parse(it) }.sorted()
        var longest = 1
        var current = 1
        for (i in 1 until sorted.size) {
            if (sorted[i] == sorted[i - 1].plusDays(1)) {
                current++
                longest = maxOf(longest, current)
            } else if (sorted[i] != sorted[i - 1]) {
                current = 1
            }
        }
        return longest
    }
}
