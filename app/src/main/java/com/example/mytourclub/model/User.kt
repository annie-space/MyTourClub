package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dob: Date,
    val email: String,
    val phone: String,
    val healthGroup: String,
    val password: String,
    val gender: String
)
