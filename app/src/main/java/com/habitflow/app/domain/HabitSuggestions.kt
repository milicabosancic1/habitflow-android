package com.habitflow.app.domain

data class HabitSuggestion(
    val name: String,
    val category: String,
    val trackingType: TrackingType,
    val unit: String? = null,
    val incrementAmount: Int? = null,
    val targetCount: Int = 1
)

val SUGGESTED_HABITS = listOf(
    HabitSuggestion("Pij vodu", "Zdravlje", TrackingType.QUANTITY, unit = "ml", incrementAmount = 250, targetCount = 2000),
    HabitSuggestion("Hodaj (koraci)", "Zdravlje", TrackingType.NUMERIC, unit = "koraka", targetCount = 8000),
    HabitSuggestion("Vežbaj", "Zdravlje", TrackingType.SIMPLE),
    HabitSuggestion("Čitaj", "Učenje", TrackingType.SIMPLE),
    HabitSuggestion("Meditiraj", "Um", TrackingType.SIMPLE)
)
