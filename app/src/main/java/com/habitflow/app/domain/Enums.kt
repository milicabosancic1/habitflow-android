package com.habitflow.app.domain

enum class HabitType { BUILD, QUIT }
enum class TrackingType { SIMPLE, QUANTITY, NUMERIC }
enum class FrequencyType { DAILY, SPECIFIC_DAYS, TIMES_PER_WEEK }
enum class EntryStatus { DONE, MISSED, PARTIAL }
enum class SyncStatus { SYNCED, PENDING, PENDING_DELETE }
enum class RecommendationType { OPTIMAL_TIME, MAKE_EASIER, STREAK_WARNING, NEW_HABIT, WEEKLY_INSIGHT }
enum class AchievementType { FIRST_HABIT, STREAK_7, STREAK_30, PERFECT_WEEK, COMEBACK }
