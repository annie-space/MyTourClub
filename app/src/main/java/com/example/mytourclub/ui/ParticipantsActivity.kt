package com.example.mytourclub.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.ParticipantAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.User
import kotlinx.coroutines.launch

class ParticipantsActivity : AppCompatActivity() {

    private var routeId: Int = -1
    private lateinit var participantAdapter: ParticipantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participants)

        routeId = intent.getIntExtra("routeId", -1)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewParticipants)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@ParticipantsActivity)
            val bookings = db.bookingDao().getBookingsByRouteId(routeId)
            val participants = bookings.mapNotNull { db.userDao().getUserById(it.userId) }

            participantAdapter = ParticipantAdapter(participants)
            recyclerView.adapter = participantAdapter
        }
    }
}
