package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.AdminRouteAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageRoutesActivity : AppCompatActivity(), AdminRouteAdapter.OnItemClickListener {

    private lateinit var routeAdapter: AdminRouteAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_routes)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRoutes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this@ManageRoutesActivity)

        findViewById<Button>(R.id.btnAddRoute).setOnClickListener {
            val intent = Intent(this, AddRouteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_ROUTE)
        }

        lifecycleScope.launch {
            loadRoutes()
        }
    }

    private suspend fun loadRoutes() {
        val routes = withContext(Dispatchers.IO) { db.routeDao().getAllRoutes() }
        val routesWithEquipment = routes.map { route ->
            val routeEquipment = db.routeEquipmentDao().getEquipmentByRoute(route.id)
            val routeEquipmentWithDetails = routeEquipment.map { routeEquip ->
                routeEquip to db.equipmentDao().getEquipmentById(routeEquip.equipmentId)
            }
            route to routeEquipmentWithDetails
        }

        withContext(Dispatchers.Main) {
            routeAdapter = AdminRouteAdapter(routesWithEquipment, this@ManageRoutesActivity)
            findViewById<RecyclerView>(R.id.recyclerViewRoutes).adapter = routeAdapter
        }
    }

    override fun onItemClick(route: Route) {
        val intent = Intent(this, EditRouteActivity::class.java)
        intent.putExtra("routeId", route.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT_ROUTE)
    }

    override fun onDeleteClick(route: Route) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Удаление связанных записей перед удалением маршрута
                val routeEquipmentList = db.routeEquipmentDao().getEquipmentByRoute(route.id)
                routeEquipmentList.forEach { routeEquipment ->
                    db.rentDao().deleteByRouteEquipmentId(routeEquipment.id)
                    db.routeEquipmentDao().deleteByRouteEquipmentId(routeEquipment.id)
                }
                db.routeDao().deleteBookingsByRouteId(route.id)
                db.routeDao().deleteReviewsByRouteId(route.id)
                db.routeDao().deleteRoute(route)
            }
            loadRoutes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_ROUTE && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadRoutes()
            }
        } else if (requestCode == REQUEST_CODE_EDIT_ROUTE && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadRoutes()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_ROUTE = 1
        private const val REQUEST_CODE_EDIT_ROUTE = 2
    }
}
