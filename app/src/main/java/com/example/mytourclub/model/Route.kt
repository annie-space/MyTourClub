package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.mytourclub.until.Difficulty
import com.example.mytourclub.until.Direction
import com.example.mytourclub.until.Typing
import java.io.Serializable
import java.util.Date

@Entity(
    tableName = "routes",
    foreignKeys = [
        ForeignKey(
            entity = Admin::class,
            parentColumns = ["id"],
            childColumns = ["admin_id"],
            onDelete = ForeignKey.SET_DEFAULT
        ),
        ForeignKey(
            entity = Guide::class,
            parentColumns = ["id"],
            childColumns = ["guide1_id"],
            onDelete = ForeignKey.SET_DEFAULT
        ),
        ForeignKey(
            entity = Guide::class,
            parentColumns = ["id"],
            childColumns = ["guide2_id"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ]
)
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val direction: Direction,
    val difficulty: Difficulty,
    val duration: Int,
    val dates: Date,
    val length: Int,
    val photoResId: Int?,
    val description: String,
    val places: Int,
    val available_places: Int,
    val type: Typing,
    val admin_id: Int = 1, // Значение по умолчанию
    val guide1_id: Int = 1, // Значение по умолчанию
    val guide2_id: Int = 1, // Значение по умолчанию
    val price: Double
) : Serializable
