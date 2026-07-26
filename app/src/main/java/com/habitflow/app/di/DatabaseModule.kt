package com.habitflow.app.di

import android.content.Context
import androidx.room.Room
import com.habitflow.app.data.local.AchievementDao
import com.habitflow.app.data.local.AppDatabase
import com.habitflow.app.data.local.HabitDao
import com.habitflow.app.data.local.HabitEntryDao
import com.habitflow.app.data.local.MIGRATION_1_2
import com.habitflow.app.data.local.MIGRATION_2_3
import com.habitflow.app.data.local.RecommendationDao
import com.habitflow.app.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "habitflow.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()
    @Provides fun provideEntryDao(db: AppDatabase): HabitEntryDao = db.habitEntryDao()
    @Provides fun provideRecommendationDao(db: AppDatabase): RecommendationDao = db.recommendationDao()
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()
}
