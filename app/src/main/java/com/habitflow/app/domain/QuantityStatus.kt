package com.habitflow.app.domain

/** Status unosa za navike sa količinom/brojem — čista logika, laka za testiranje. */
object QuantityStatus {
    fun statusFor(value: Int, target: Int): EntryStatus = when {
        value <= 0 -> EntryStatus.MISSED
        value >= target -> EntryStatus.DONE
        else -> EntryStatus.PARTIAL
    }
}
