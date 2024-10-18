package com.example.gravitygame.database

import androidx.room.TypeConverter
import com.example.gravitygame.ui.utils.BattleResultEnum

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