package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.BookedRouteAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Booking
import com.example.mytourclub.util.UserManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class UserActivity : AppCompatActivity(), BookedRouteAdapter.OnItemClickListener {

    private lateinit var bookedRouteAdapter: BookedRouteAdapter
    private var selectedItemId: Int = R.id.navigation_user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Извлечение данных из SharedPreferences и установка в UserManager
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val userRole = sharedPreferences.getString("userRole", null)
        UserManager.setUser(userId, userRole)

        selectedItemId = intent.getIntExtra("selectedItemId", R.id.navigation_user)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBookedRoutes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_user -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish() // Завершаем текущую активность, чтобы перезагрузить
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@UserActivity)
            val bookings = db.bookingDao().getBookingsByUserId(UserManager.userId)
            val routes = db.routeDao().getAllRoutes() // Загружаем все маршруты для отображения названий
            Log.d("UserActivity", "Loaded bookings from DB: $bookings")

            bookedRouteAdapter = BookedRouteAdapter(bookings, routes, this@UserActivity, { booking ->
                deleteBooking(booking)
            })
            recyclerView.adapter = bookedRouteAdapter
        }
    }

    override fun onItemClick(booking: Booking) {
        val intent = Intent(this, BookingDetailActivity::class.java)
        intent.putExtra("routeId", booking.routeId)
        intent.putExtra("bookingId", booking.id)
        startActivity(intent)
    }

    private fun deleteBooking(booking: Booking) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@UserActivity)
            db.bookingDao().delete(booking)
            db.rentDao().deleteByBookingId(booking.id)
            // Обновление UI после удаления
            val updatedBookings = db.bookingDao().getBookingsByUserId(UserManager.userId)
            val routes = db.routeDao().getAllRoutes()
            bookedRouteAdapter = BookedRouteAdapter(updatedBookings, routes, this@UserActivity, { booking ->
                deleteBooking(booking)
            })
            findViewById<RecyclerView>(R.id.recyclerViewBookedRoutes).adapter = bookedRouteAdapter
        }
    }
}
