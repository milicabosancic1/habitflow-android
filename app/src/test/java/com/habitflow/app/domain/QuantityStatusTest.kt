package com.habitflow.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class QuantityStatusTest {

    @Test
    fun `MISSED kad je vrednost nula ili manja`() {
        assertEquals(EntryStatus.MISSED, QuantityStatus.statusFor(0, 2000))
    }

    @Test
    fun `PARTIAL kad ima napretka ali ispod cilja`() {
        assertEquals(EntryStatus.PARTIAL, QuantityStatus.statusFor(500, 2000))
    }

    @Test
    fun `DONE kad vrednost dostigne ili premasi cilj`() {
        assertEquals(EntryStatus.DONE, QuantityStatus.statusFor(2000, 2000))
        assertEquals(EntryStatus.DONE, QuantityStatus.statusFor(2500, 2000))
    }
}
