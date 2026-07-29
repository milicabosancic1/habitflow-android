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
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitEntryDao(): HabitEntryDao
    abstract fun recommendationDao(): RecommendationDao
    abstract fun userDao(): UserDao
    abstract fun achievementDao(): AchievementDao
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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE recommendations ADD COLUMN category TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE habits ADD COLUMN trackingType TEXT NOT NULL DEFAULT 'SIMPLE'")
        db.execSQL("ALTER TABLE habits ADD COLUMN unit TEXT")
        db.execSQL("ALTER TABLE habits ADD COLUMN incrementAmount INTEGER")
    }
}
