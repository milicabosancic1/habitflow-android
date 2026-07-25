package com.habitflow.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitEntryDao {

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId ORDER BY date DESC")
    fun observeForHabit(habitId: String): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE date = :date")
    fun observeForDate(date: String): Flow<List<HabitEntryEntity>>

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getForHabitAndDate(habitId: String, date: String): HabitEntryEntity?

    @Query("SELECT * FROM habit_entries WHERE habitId = :habitId ORDER BY date DESC")
    suspend fun getAllForHabit(habitId: String): List<HabitEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: HabitEntryEntity)

    @Query("SELECT * FROM habit_entries WHERE syncStatus != 'SYNCED'")
    suspend fun getPending(): List<HabitEntryEntity>

    @Query("UPDATE habit_entries SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
