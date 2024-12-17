package com.example.mytourclub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mytourclub.model.Review

@Dao
interface ReviewDao {
    @Insert
    suspend fun insert(review: Review): Long

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Int): Review?

    @Query("SELECT * FROM reviews WHERE routeId = :routeId")
    suspend fun getReviewsByRouteId(routeId: Int): List<Review>

    @Update
    suspend fun update(review: Review)

    @Delete
    suspend fun delete(review: Review)

    @Query("DELETE FROM reviews WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Int)
}
