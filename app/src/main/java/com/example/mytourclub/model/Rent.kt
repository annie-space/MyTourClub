package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mytourclub.until.Converters
import java.util.Date

@Entity(
    tableName = "rents",
    foreignKeys = [ForeignKey(
        entity = Booking::class,
        parentColumns = ["id"],
        childColumns = ["bookingId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Rent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val routeEquipmentId: Int,
    val rentalDate: Date,
    val returnDate: Date?,
    val price: Double,
    val quantity: Int // Новое поле для количества арендованных единиц
)

