package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import com.example.mytourclub.model.Equipment

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment")
    suspend fun getAllEquipment(): List<Equipment>

    @Query("SELECT * FROM equipment WHERE id = :id")
    suspend fun getEquipmentById(id: Int): Equipment?

    @Insert
    suspend fun insertAll(equipment: List<Equipment>)

    @Insert
    suspend fun insertEquipment(equipment: Equipment)

    @Update
    suspend fun update(equipment: Equipment)

    @Query("DELETE FROM equipment")
    suspend fun deleteAllEquipment()

    @Delete
    suspend fun deleteEquipment(equipment: Equipment)

    @Query("DELETE FROM route_equipment WHERE equipmentId = :equipmentId")
    suspend fun deleteRouteEquipmentByEquipmentId(equipmentId: Int)



}
