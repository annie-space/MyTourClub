package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mytourclub.model.BookingRentCrossRef

@Dao
interface BookingRentCrossRefDao {
    @Insert
    suspend fun insert(crossRef: BookingRentCrossRef): Long

    @Query("SELECT * FROM booking_rents WHERE bookingId = :bookingId")
    suspend fun getRentsByBookingId(bookingId: Int): List<BookingRentCrossRef>

    @Delete
    suspend fun delete(crossRef: BookingRentCrossRef)
}
