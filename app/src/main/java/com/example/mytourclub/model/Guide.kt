package com.example.mytourclub.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guides")
data class Guide (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val experience: Int,
    val specialization: String,
    val password: String,
    val gender: String
)