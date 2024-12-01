package com.marks2games.gravitygame.database

import androidx.room.TypeConverter
import com.marks2games.gravitygame.ui.utils.BattleResultEnum

class Converters {
    @TypeConverter
    fun fromBattleResultEnum(value: BattleResultEnum): String {
        return value.name
    }

    @TypeConverter
    fun toBattleResultEnum(value: String): BattleResultEnum {
        return BattleResultEnum.valueOf(value)
    }
}