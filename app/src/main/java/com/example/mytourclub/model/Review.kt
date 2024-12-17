package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mytourclub.until.Converters
import com.example.mytourclub.until.Difficulty
import java.util.Date

@Entity(
    tableName = "reviews",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Route::class,
        parentColumns = ["id"],
        childColumns = ["routeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val routeId: Int,
    val reviewDate: Date,
    val rating: Int,
    val comment: String
)
