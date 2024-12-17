package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mytourclub.model.RouteEquipment

@Dao
interface RouteEquipmentDao {
    @Query("SELECT * FROM route_equipment")
    suspend fun getAllRouteEquipment(): List<RouteEquipment>

    @Query("SELECT * FROM route_equipment WHERE routeId = :routeId")
    suspend fun getEquipmentForRoute(routeId: Int): List<RouteEquipment>

    @Insert
    suspend fun insertAll(routeEquipment: List<RouteEquipment>)

    @Query("DELETE FROM route_equipment")
    suspend fun deleteAllRouteEquipment()

    @Query("DELETE FROM route_equipment WHERE equipmentId = :equipmentId")
    suspend fun deleteByEquipmentId(equipmentId: Int)

    @Query("SELECT * FROM route_equipment WHERE routeId = :routeId")
    suspend fun getEquipmentByRoute(routeId: Int): List<RouteEquipment>

    @Query("DELETE FROM route_equipment WHERE id = :routeEquipmentId")
    suspend fun deleteByRouteEquipmentId(routeEquipmentId: Int)

    @Query("SELECT * FROM route_equipment WHERE equipmentId = :equipmentId")
    suspend fun getRouteEquipmentForEquipment(equipmentId: Int): List<RouteEquipment>

    @Query("SELECT * FROM route_equipment WHERE equipmentId = :equipmentId AND routeId = :routeId LIMIT 1")
    suspend fun getRouteEquipmentForEquipmentAndRoute(equipmentId: Int, routeId: Int): RouteEquipment


}
