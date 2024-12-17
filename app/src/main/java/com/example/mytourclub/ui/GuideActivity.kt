package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.GuidRouteAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Route
import com.example.mytourclub.util.UserManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class GuideActivity : AppCompatActivity(), GuidRouteAdapter.OnItemClickListener {

    private lateinit var routeAdapter: GuidRouteAdapter
    private var selectedItemId: Int = R.id.navigation_user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        // Получение данных из UserManager
        val guideId = UserManager.userId
        val userRole = UserManager.userRole
        selectedItemId = intent.getIntExtra("selectedItemId", R.id.navigation_user)

        if (guideId == -1 || userRole.isNullOrEmpty()) {
            Log.e("GuideActivity", "Не удалось получить guideId или userRole из UserManager")
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRoutes)
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
                    val intent = Intent(this, GuideActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish() // Завершаем текущую активность, чтобы перезагрузить
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("selectedItemId", R.id.navigation_profile)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@GuideActivity)
            val routes = db.routeDao().getRoutesByGuideId(guideId).sortedBy { it.dates }
            Log.d("GuideActivity", "Loaded routes from DB: $routes")

            routeAdapter = GuidRouteAdapter(routes, this@GuideActivity)
            recyclerView.adapter = routeAdapter
        }
    }

    override fun onItemClick(route: Route) {
        val intent = Intent(this, RouteDetailActivity::class.java)
        intent.putExtra("routeId", route.id)
        startActivity(intent)
    }

    override fun onParticipantsClick(route: Route) {
        val intent = Intent(this, ParticipantsActivity::class.java)
        intent.putExtra("routeId", route.id)
        startActivity(intent)
    }
}
