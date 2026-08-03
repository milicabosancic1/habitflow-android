package com.habitflow.app.domain

data class HabitSuggestion(
    val name: String,
    val category: String,
    val trackingType: TrackingType,
    val unit: String? = null,
    val incrementAmount: Int? = null,
    val targetCount: Int = 1,
    val color: String = HABIT_COLORS[0]
)

val SUGGESTED_HABITS = listOf(
    HabitSuggestion("Pij vodu", "Zdravlje", TrackingType.QUANTITY, unit = "ml", incrementAmount = 250, targetCount = 2000, color = HABIT_COLORS[2]),
    HabitSuggestion("Hodaj (koraci)", "Zdravlje", TrackingType.NUMERIC, unit = "koraka", targetCount = 8000, color = HABIT_COLORS[0]),
    HabitSuggestion("Vežbaj", "Zdravlje", TrackingType.SIMPLE, color = HABIT_COLORS[1]),
    HabitSuggestion("Čitaj", "Učenje", TrackingType.SIMPLE, color = HABIT_COLORS[5]),
    HabitSuggestion("Meditiraj", "Um", TrackingType.SIMPLE, color = HABIT_COLORS[6])
)
