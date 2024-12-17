package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytourclub.model.Rent
import java.util.Date

@Dao
interface RentDao {
    @Insert
    suspend fun insert(rent: Rent): Long

    @Query("SELECT * FROM rents WHERE bookingId = :bookingId")
    suspend fun getRentsByBookingId(bookingId: Int): List<Rent>

    @Query("SELECT * FROM rents WHERE id = :rentId")
    suspend fun getRentById(rentId: Int): Rent?

    @Update
    suspend fun update(rent: Rent)

    @Query("DELETE FROM rents")
    suspend fun deleteAllRents()

    @Query("DELETE FROM rents WHERE routeEquipmentId = :routeEquipmentId")
    suspend fun deleteByRouteEquipmentId(routeEquipmentId: Int)

    @Delete
    suspend fun delete(rent: Rent)

    @Query(""" 
        SELECT COALESCE(SUM(quantity), 0) 
        FROM rents 
        WHERE routeEquipmentId = :routeEquipmentId 
        AND ((rentalDate BETWEEN :startDate AND :endDate) 
            OR (returnDate BETWEEN :startDate AND :endDate) 
            OR (:startDate BETWEEN rentalDate AND returnDate) 
            OR (:endDate BETWEEN rentalDate AND returnDate)) 
    """)
    suspend fun getRentedQuantity(routeEquipmentId: Int, startDate: Date, endDate: Date): Int

    @Query("DELETE FROM rents WHERE bookingId = :bookingId")
    suspend fun deleteByBookingId(bookingId: Int)
}
