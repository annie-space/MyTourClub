package com.example.mytourclub.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.ReviewAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Review
import com.example.mytourclub.util.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class WriteReviewActivity : AppCompatActivity() {

    private var routeId: Int = -1
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_review)

        // Получение данных из UserManager
        val userId = UserManager.userId
        routeId = intent.getIntExtra("routeId", -1)

        if (userId == -1 || routeId == -1) {
            Toast.makeText(this, "Неверный ID пользователя или маршрута", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvRouteName = findViewById<TextView>(R.id.tvRouteName)
        val etReviewText = findViewById<EditText>(R.id.etReviewText)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val btnSubmitReview = findViewById<Button>(R.id.btnSubmitReview)
        val recyclerViewReviews = findViewById<RecyclerView>(R.id.recyclerViewReviews)

        recyclerViewReviews.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@WriteReviewActivity)
            val route = db.routeDao().getRouteById(routeId)
            val reviews = withContext(Dispatchers.IO) { db.reviewDao().getReviewsByRouteId(routeId) }

            route?.let {
                tvRouteName.text = it.name
            }

            reviewAdapter = ReviewAdapter(reviews.toMutableList())
            recyclerViewReviews.adapter = reviewAdapter
        }

        btnSubmitReview.setOnClickListener {
            val reviewText = etReviewText.text.toString()
            val rating = ratingBar.rating.toInt()

            if (reviewText.isNotBlank()) {
                val review = Review(
                    userId = userId,
                    routeId = routeId,
                    reviewDate = Date(),
                    rating = rating,
                    comment = reviewText
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(this@WriteReviewActivity)
                    db.reviewDao().insert(review)
                    withContext(Dispatchers.Main) {
                        reviewAdapter.addReview(review)
                        Toast.makeText(this@WriteReviewActivity, "Отзыв отправлен", Toast.LENGTH_SHORT).show()
                        etReviewText.text.clear()
                        ratingBar.rating = 0f
                    }
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите текст отзыва", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
