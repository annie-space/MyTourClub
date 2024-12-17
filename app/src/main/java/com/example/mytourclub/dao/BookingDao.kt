package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytourclub.model.Booking

@Dao
interface BookingDao {
    @Insert
    suspend fun insert(booking: Booking): Long

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: Int): Booking?

    @Query("SELECT * FROM bookings WHERE routeId = :routeId")
    suspend fun getBookingsByRouteId(routeId: Int): List<Booking>

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun delete(booking: Booking)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()

    @Query("DELETE FROM bookings WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    suspend fun getBookingsByUserId(userId: Int): List<Booking>

    @Query("SELECT * FROM bookings WHERE routeId = :routeId AND status = :status")
    suspend fun getBookingsByRouteAndStatus(routeId: Int, status: Boolean): List<Booking>
}
