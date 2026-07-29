package com.habitflow.app.domain

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

class GreetingHelperTest {

    @Test
    fun `jutro izmedju 5 i 11h`() {
        assertEquals("Dobro jutro", GreetingHelper.greeting(LocalTime.of(8, 0)))
    }

    @Test
    fun `dan izmedju 12 i 17h`() {
        assertEquals("Dobar dan", GreetingHelper.greeting(LocalTime.of(14, 30)))
    }

    @Test
    fun `vece van jutra i dana`() {
        assertEquals("Dobro veče", GreetingHelper.greeting(LocalTime.of(20, 0)))
        assertEquals("Dobro veče", GreetingHelper.greeting(LocalTime.of(2, 0)))
    }
}
