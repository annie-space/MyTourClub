package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class BookingDetailActivity : AppCompatActivity() {

    private var routeId: Int = -1
    private var bookingId: Int = -1
    private var userId: Int = -1
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_detail)

        // Получение данных из Intent
        routeId = intent.getIntExtra("routeId", -1)
        bookingId = intent.getIntExtra("bookingId", -1)
        userId = intent.getIntExtra("userId", -1)

        // Загрузка данных маршрута из базы данных
        loadRouteDetails()

        val recyclerViewReviews = findViewById<RecyclerView>(R.id.recyclerViewReviews)
        recyclerViewReviews.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@BookingDetailActivity)
            val reviews = db.reviewDao().getReviewsByRouteId(routeId)

            reviewAdapter = ReviewAdapter(reviews.toMutableList())
            recyclerViewReviews.adapter = reviewAdapter
        }
    }

    private fun loadRouteDetails() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@BookingDetailActivity)
            val route = db.routeDao().getRouteById(routeId)
            val booking = db.bookingDao().getBookingById(bookingId)

            route?.let { r ->
                // Установка данных в элементы макета
                findViewById<TextView>(R.id.tvRouteNameDetail).text = r.name

                // Форматирование даты
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = r.dates.let { sdf.format(it) }
                findViewById<TextView>(R.id.tvRouteDateDetail).text = formattedDate

                findViewById<TextView>(R.id.tvRouteLengthDetail).text = "${r.length} км"
                findViewById<TextView>(R.id.tvRouteDurationDetail).text = "${r.duration} дней"
                findViewById<TextView>(R.id.tvRouteDescriptionDetail).text = r.description
                findViewById<TextView>(R.id.tvRouteDirectionDetail).text = "${r.direction.value}"
                findViewById<TextView>(R.id.tvRouteDifficultyDetail).text = "${r.difficulty.value}/5"
                findViewById<TextView>(R.id.tvRouteTypDetail).text = "${r.type.value}"
                findViewById<TextView>(R.id.tvRoutePlacesDetail).text = "Мест: ${r.places}"

                // Установка фото маршрута
                r.photoResId?.let {
                    findViewById<ImageView>(R.id.imageViewRouteDetail).setImageResource(it)
                }

                // Загрузка информации о гидах
                loadGuideInfo(r.guide1_id, r.guide2_id)

                findViewById<Button>(R.id.btnWriteReview).setOnClickListener {
                    if (booking?.status == false) {
                        Toast.makeText(this@BookingDetailActivity, "Дождитесь подтверждения брони", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(this@BookingDetailActivity, WriteReviewActivity::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("routeId", routeId)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun loadGuideInfo(guide1Id: Int, guide2Id: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@BookingDetailActivity)
            val guide1 = db.guideDao().getGuideById(guide1Id)
            val guide2 = db.guideDao().getGuideById(guide2Id)

            guide1?.let {
                findViewById<TextView>(R.id.tvGuide1).text = " ${it.name} ${it.phone}"
            }
            guide2?.let {
                findViewById<TextView>(R.id.tvGuide2).text = "${it.name} ${it.phone}"
            }
        }
    }
}
