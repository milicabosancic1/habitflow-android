package com.habitflow.app.domain

import java.time.LocalTime

/** Pozdrav prema dobu dana — čista logika, laka za jedinično testiranje. */
object GreetingHelper {
    fun greeting(now: LocalTime = LocalTime.now()): String = when (now.hour) {
        in 5..11 -> "Dobro jutro"
        in 12..17 -> "Dobar dan"
        else -> "Dobro veče"
    }
}
