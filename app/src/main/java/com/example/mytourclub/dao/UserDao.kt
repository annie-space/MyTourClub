package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mytourclub.model.Guide
import com.example.mytourclub.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET email = :email, phone = :phone, healthGroup = :healthGroup, password = :password WHERE id = :userId")
    suspend fun updateUser(userId: Int, email: String, phone: String, healthGroup: String, password: String)

}
