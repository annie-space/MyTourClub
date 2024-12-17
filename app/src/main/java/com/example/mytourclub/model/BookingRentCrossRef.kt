package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "booking_rents",
    primaryKeys = ["bookingId", "rentId"],
    foreignKeys = [
        ForeignKey(entity = Booking::class, parentColumns = ["id"], childColumns = ["bookingId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Rent::class, parentColumns = ["id"], childColumns = ["rentId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class BookingRentCrossRef(
    val bookingId: Int,
    val rentId: Int
)
