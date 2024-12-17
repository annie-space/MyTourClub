package com.example.mytourclub.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.RouteBookingAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Booking
import com.example.mytourclub.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmBookingsActivity : AppCompatActivity(), RouteBookingAdapter.OnItemClickListener {

    private lateinit var routeBookingAdapter: RouteBookingAdapter
    private lateinit var db: AppDatabase
    private lateinit var userNames: Map<Int, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_bookings)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRoutes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this@ConfirmBookingsActivity)

        lifecycleScope.launch {
            loadUserNames()
            loadRoutesAndBookings()
        }
    }

    private suspend fun loadUserNames() {
        val users = withContext(Dispatchers.IO) { db.userDao().getAllUsers() }
        userNames = users.associateBy({ it.id }, { it.name })
    }

    private suspend fun loadRoutesAndBookings() {
        val routes = withContext(Dispatchers.IO) { db.routeDao().getAllRoutes() }

        // Получение бронирований со статусом = 0 для каждого маршрута
        val routeBookings = routes.map { route ->
            val pendingBookings = db.bookingDao().getBookingsByRouteAndStatus(route.id, status = false)
            route to pendingBookings.toMutableList()
        }.toMutableList()

        withContext(Dispatchers.Main) {
            routeBookingAdapter = RouteBookingAdapter(routeBookings, userNames, this@ConfirmBookingsActivity)
            findViewById<RecyclerView>(R.id.recyclerViewRoutes).adapter = routeBookingAdapter
        }
    }

    override fun onConfirmClick(booking: Booking) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.bookingDao().updateBooking(booking.copy(status = true))
            withContext(Dispatchers.Main) {
                routeBookingAdapter.updateBooking(booking.copy(status = true))
            }
        }
    }
}
