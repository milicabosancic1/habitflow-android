package com.habitflow.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM recommendations WHERE userId = :userId AND isDismissed = 0 ORDER BY createdAt DESC")
    fun observeActive(userId: String): Flow<List<RecommendationEntity>>

    @Query("SELECT * FROM recommendations WHERE userId = :userId AND isDismissed = 0 ORDER BY createdAt DESC")
    suspend fun getActiveOnce(userId: String): List<RecommendationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rec: RecommendationEntity)

    @Query("UPDATE recommendations SET isDismissed = 1 WHERE id = :id")
    suspend fun dismiss(id: String)
}
