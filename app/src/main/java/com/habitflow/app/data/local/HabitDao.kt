package com.habitflow.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE userId = :userId AND isArchived = 0 ORDER BY createdAt DESC")
    fun observeActive(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId AND isArchived = 0 ORDER BY createdAt DESC")
    suspend fun getActive(userId: String): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: String): HabitEntity?

    @Query("SELECT * FROM habits WHERE id = :id")
    fun observeById(id: String): Flow<HabitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE syncStatus != 'SYNCED'")
    suspend fun getPending(): List<HabitEntity>

    @Query("UPDATE habits SET userId = :newUserId, syncStatus = 'PENDING' WHERE userId = :oldUserId")
    suspend fun reassignOwner(oldUserId: String, newUserId: String)

    @Query("UPDATE habits SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}
