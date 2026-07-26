package com.habitflow.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY unlockedAt DESC")
    fun observeAll(userId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE userId = :userId")
    suspend fun getAllOnce(userId: String): List<AchievementEntity>

    @Insert
    suspend fun insert(achievement: AchievementEntity)
}
