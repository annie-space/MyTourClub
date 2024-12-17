package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mytourclub.model.Admin

@Dao
interface AdminDao {
    @Insert
    suspend fun insertAdmin(admin: Admin)

    @Query("SELECT * FROM admins WHERE email = :email AND password = :password")
    suspend fun getAdminByEmailAndPassword(email: String, password: String): Admin?

    @Query("SELECT * FROM admins WHERE name = :name")
    suspend fun getAdminByName(name: String): Admin?

    @Query("SELECT * FROM admins WHERE id = :adminId")
    suspend fun getAdminById(adminId: Int): Admin?
}
