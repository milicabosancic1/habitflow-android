package com.habitflow.app.data.local

import androidx.room.TypeConverter
import com.habitflow.app.domain.*

class Converters {
    @TypeConverter fun habitType(v: HabitType) = v.name
    @TypeConverter fun toHabitType(v: String) = HabitType.valueOf(v)

    @TypeConverter fun freqType(v: FrequencyType) = v.name
    @TypeConverter fun toFreqType(v: String) = FrequencyType.valueOf(v)

    @TypeConverter fun entryStatus(v: EntryStatus) = v.name
    @TypeConverter fun toEntryStatus(v: String) = EntryStatus.valueOf(v)

    @TypeConverter fun syncStatus(v: SyncStatus) = v.name
    @TypeConverter fun toSyncStatus(v: String) = SyncStatus.valueOf(v)

    @TypeConverter fun recType(v: RecommendationType) = v.name
    @TypeConverter fun toRecType(v: String) = RecommendationType.valueOf(v)

    @TypeConverter fun achType(v: AchievementType) = v.name
    @TypeConverter fun toAchType(v: String) = AchievementType.valueOf(v)
}
