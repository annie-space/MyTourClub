package com.example.mytourclub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ReviewAdapter(
    private val reviews: MutableList<Review>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // Получение имени пользователя из базы данных
        val userName = runBlocking {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(holder.itemView.context)
                val user = db.userDao().getUserById(review.userId)
                user?.name ?: "Аноним"
            }
        }

        holder.tvUserName.text = userName
        holder.ratingBarReview.rating = review.rating.toFloat()
        holder.tvReviewText.text = review.comment // Используем правильное поле
    }

    override fun getItemCount(): Int = reviews.size

    fun addReview(review: Review) {
        reviews.add(review)
        notifyItemInserted(reviews.size - 1)
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val ratingBarReview: RatingBar = itemView.findViewById(R.id.ratingBarReview)
        val tvReviewText: TextView = itemView.findViewById(R.id.tvReviewText)
    }
}
