package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "route_equipment",
    foreignKeys = [
        ForeignKey(
            entity = Route::class,
            parentColumns = ["id"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Equipment::class,
            parentColumns = ["id"],
            childColumns = ["equipmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RouteEquipment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val routeId: Int,
    val equipmentId: Int,
    val quantityRequired: Int
)

