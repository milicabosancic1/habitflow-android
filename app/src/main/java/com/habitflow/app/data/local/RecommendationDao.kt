package com.habitflow.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM recommendations WHERE isDismissed = 0 ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<RecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rec: RecommendationEntity)

    @Query("UPDATE recommendations SET isDismissed = 1 WHERE id = :id")
    suspend fun dismiss(id: String)

    @Query("SELECT COUNT(*) FROM recommendations WHERE type = :type AND habitId = :habitId AND isDismissed = 0")
    suspend fun countActiveOfType(type: String, habitId: String?): Int
}
