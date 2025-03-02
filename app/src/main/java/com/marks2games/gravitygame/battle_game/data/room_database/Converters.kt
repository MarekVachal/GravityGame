package com.marks2games.gravitygame.battle_game.data.room_database

import androidx.room.TypeConverter
import com.marks2games.gravitygame.battle_game.data.model.enum_class.BattleResultEnum

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