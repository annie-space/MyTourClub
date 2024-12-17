package com.example.mytourclub.until

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Конвертеры для даты
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { dateFormat.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.let { dateFormat.format(it) }
    }

    // Конвертеры для Direction
    @TypeConverter
    fun fromDirection(direction: Direction): String {
        return direction.value
    }

    @TypeConverter
    fun toDirection(value: String): Direction {
        return when(value) {
            "Кавказ" -> Direction.KAVKAZ
            "Север" -> Direction.NORTH
            "Карелия" -> Direction.KARELIA
            "Алтай" -> Direction.ALTAI
            "Дальний восток" -> Direction.EAST
            "Сибирь" -> Direction.SIBERIA
            "Урал" -> Direction.URAL

            else -> throw IllegalArgumentException("Unknown direction")
        }
    }

    // Конвертеры для Difficulty
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): Int {
        return difficulty.value
    }

    @TypeConverter
    fun toDifficulty(value: Int): Difficulty {
        return when(value) {
            1 -> Difficulty.ONE
            2 -> Difficulty.TWO
            3 -> Difficulty.THREE
            4 -> Difficulty.FOUR
            5 -> Difficulty.FIVE
            else -> throw IllegalArgumentException("Unknown difficulty")
        }
    }

    // Конвертеры для EquipmentType
    @TypeConverter
    fun fromEquipmentType(equipmentType: EquipmentType): String {
        return equipmentType.name
    }

    @TypeConverter
    fun toEquipmentType(value: String): EquipmentType {
        return EquipmentType.valueOf(value)
    }
}
