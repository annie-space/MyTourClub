package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import com.example.mytourclub.model.Equipment
import com.example.mytourclub.model.Guide

@Dao
interface GuideDao {

    @Query("SELECT * FROM guides WHERE id = :id")
    suspend fun getGuideById(id: Int): Guide?

    @Query("SELECT * FROM guides WHERE email = :email AND password = :password")
    suspend fun getGuideByEmailAndPassword(email: String, password: String): Guide?

    @Query("SELECT * FROM guides WHERE name = :name")
    suspend fun getGuideByName(name: String): Guide?

    @Query("SELECT * FROM guides")
    suspend fun getAllGuides(): List<Guide>


    @Insert
    suspend fun insertGuide(guide: Guide)

    @Update
    suspend fun update(guide: Guide)

    @Delete
    suspend fun deleteGuide(guide: Guide)
}
