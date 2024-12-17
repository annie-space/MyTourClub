package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.ReviewAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.util.UserManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RouteDetailActivity : AppCompatActivity() {

    private var routeId: Int = -1
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var tvRouteNameDetail: TextView
    private lateinit var tvRoutePriceDetail: TextView
    private lateinit var tvRouteDateDetail: TextView
    private lateinit var tvRouteLengthDetail: TextView
    private lateinit var tvRouteDurationDetail: TextView
    private lateinit var tvRouteDescriptionDetail: TextView
    private lateinit var tvRouteDirectionDetail: TextView
    private lateinit var tvRouteDifficultyDetail: TextView
    private lateinit var tvRouteTypDetail: TextView
    private lateinit var tvRoutePlacesDetail: TextView
    private lateinit var imageViewRouteDetail: ImageView
    private lateinit var recyclerViewReviews: RecyclerView
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_detail)

        // Initialize TextView elements
        tvRouteNameDetail = findViewById(R.id.tvRouteNameDetail)
        tvRoutePriceDetail = findViewById(R.id.tvRoutePriceDetail)
        tvRouteDateDetail = findViewById(R.id.tvRouteDateDetail)
        tvRouteLengthDetail = findViewById(R.id.tvRouteLengthDetail)
        tvRouteDurationDetail = findViewById(R.id.tvRouteDurationDetail)
        tvRouteDescriptionDetail = findViewById(R.id.tvRouteDescriptionDetail)
        tvRouteDirectionDetail = findViewById(R.id.tvRouteDirectionDetail)
        tvRouteDifficultyDetail = findViewById(R.id.tvRouteDifficultyDetail)
        tvRouteTypDetail = findViewById(R.id.tvRouteTypDetail)
        tvRoutePlacesDetail = findViewById(R.id.tvRoutePlacesDetail)
        imageViewRouteDetail = findViewById(R.id.imageViewRouteDetail)
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews)

        recyclerViewReviews.layoutManager = LinearLayoutManager(this)

        // Get route ID from Intent
        routeId = intent.getIntExtra("routeId", -1)

        // Извлечение данных из UserManager
        val userId = UserManager.userId
        val userRole = UserManager.userRole

        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@RouteDetailActivity)
            val route = db.routeDao().getRouteById(routeId)
            val reviews = db.reviewDao().getReviewsByRouteId(routeId)
            val user = db.userDao().getUserById(userId)

            route?.let { r ->
                // Set data in layout elements
                tvRouteNameDetail.text = r.name
                tvRoutePriceDetail.text = "${r.price.toInt()} руб."

                // Format date
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = r.dates.let { sdf.format(it) }
                tvRouteDateDetail.text = formattedDate

                tvRouteLengthDetail.text = "${r.length} км"
                tvRouteDurationDetail.text = "${r.duration} дней"
                tvRouteDescriptionDetail.text = r.description
                tvRouteDirectionDetail.text = "${r.direction.value}"
                tvRouteDifficultyDetail.text = "${r.difficulty.value}/5"
                tvRouteTypDetail.text = "${r.type}"
                tvRoutePlacesDetail.text = "Мест: ${r.available_places}/${r.places}"

                // Set route photo
                r.photoResId?.let {
                    imageViewRouteDetail.setImageResource(it)
                }

                // Set up RecyclerView for reviews
                reviewAdapter = ReviewAdapter(reviews.toMutableList())
                recyclerViewReviews.adapter = reviewAdapter

                // Hide the "Sign Up" button for guides and admins
                val btnSignUp = findViewById<Button>(R.id.btnSignUp)
                if (userRole == "guide" || userRole == "admin") {
                    btnSignUp.visibility = View.GONE
                } else {
                    if (r.available_places == 0) {
                        btnSignUp.visibility = View.GONE
                    } else {
                        btnSignUp.setOnClickListener {
                            // Check health group and difficulty
                            if (user != null && isHealthGroupValid(user.healthGroup, r.difficulty.value)) {
                                val intent = Intent(this@RouteDetailActivity, EquipmentSelectionActivity::class.java)
                                intent.putExtra("routeId", routeId) // Передаем routeId
                                intent.putExtra("userId", userId) // Передаем userId
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@RouteDetailActivity, "По причинам безопасности вы не можете участвовать в этом маршруте", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isHealthGroupValid(healthGroup: String, difficulty: Int): Boolean {
        return when (difficulty) {
            5 -> healthGroup == "1"
            4, 3, 2 -> healthGroup == "1" || healthGroup == "2"
            1 -> healthGroup == "1" || healthGroup == "2" || healthGroup == "3"
            else -> false
        }
    }
}
