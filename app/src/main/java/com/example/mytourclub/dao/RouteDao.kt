package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytourclub.model.Route

@Dao
interface RouteDao {
    @Insert
    suspend fun insert(route: Route): Long

    @Insert
    suspend fun insertAll(routeList: List<Route>)

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: Int): Route?

    @Query("SELECT * FROM routes")
    suspend fun getAllRoutes(): List<Route>

    @Query("SELECT * FROM routes WHERE guide1_id = :guideId OR guide2_id = :guideId")
    suspend fun getRoutesByGuideId(guideId: Int): List<Route>

    @Update
    suspend fun update(route: Route)

    @Query("UPDATE routes SET available_places = :availablePlaces WHERE id = :routeId")
    suspend fun updateAvailablePlaces(routeId: Int, availablePlaces: Int)

    @Query("DELETE FROM routes WHERE admin_id = :adminId")
    suspend fun deleteRoutesByAdminId(adminId: Int)

    @Query("DELETE FROM routes WHERE guide1_id = :guideId OR guide2_id = :guideId")
    suspend fun deleteRoutesByGuideId(guideId: Int)

    @Query("DELETE FROM routes")
    suspend fun deleteAllRoutes()

    @Query("DELETE FROM bookings WHERE routeId = :routeId")
    suspend fun deleteBookingsByRouteId(routeId: Int)

    @Query("DELETE FROM reviews WHERE routeId = :routeId")
    suspend fun deleteReviewsByRouteId(routeId: Int)

    @Delete
    suspend fun deleteRoute(route: Route)

    @Query("UPDATE routes SET guide1_id = :defaultGuideId WHERE guide1_id = :oldGuideId")
    suspend fun updateGuide1(defaultGuideId: Int, oldGuideId: Int)

    @Query("UPDATE routes SET guide2_id = :defaultGuideId WHERE guide2_id = :oldGuideId")
    suspend fun updateGuide2(defaultGuideId: Int, oldGuideId: Int)
}
