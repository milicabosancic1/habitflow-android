package com.habitflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        HabitEntity::class,
        HabitEntryEntity::class,
        RecommendationEntity::class,
        AchievementEntity::class,
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao
    abstract fun recommendationDao(): RecommendationDao
    abstract fun userDao(): UserDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `email` TEXT NOT NULL, " +
                "`displayName` TEXT NOT NULL, `identityStatement` TEXT, `createdAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`id`))"
        )
    }
}
