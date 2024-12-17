package com.example.mytourclub.util

object UserManager {
    var userId: Int = -1
    var userRole: String? = null

    fun setUser(userId: Int, userRole: String?) {
        this.userId = userId
        this.userRole = userRole
    }

    fun clearUser() {
        this.userId = -1
        this.userRole = null
    }
}
