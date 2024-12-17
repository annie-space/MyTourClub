package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.AdminEquipmentAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Equipment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageEquipmentActivity : AppCompatActivity(), AdminEquipmentAdapter.OnItemClickListener {

    private lateinit var equipmentAdapter: AdminEquipmentAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_equipment)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEquipment)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this@ManageEquipmentActivity)

        findViewById<Button>(R.id.btnAddEquipment).setOnClickListener {
            val intent = Intent(this, AddEquipmentActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_EQUIPMENT)
        }

        lifecycleScope.launch {
            loadEquipment()
        }
    }

    private suspend fun loadEquipment() {
        val equipmentList = withContext(Dispatchers.IO) { db.equipmentDao().getAllEquipment() }

        withContext(Dispatchers.Main) {
            equipmentAdapter = AdminEquipmentAdapter(equipmentList, this@ManageEquipmentActivity)
            findViewById<RecyclerView>(R.id.recyclerViewEquipment).adapter = equipmentAdapter
        }
    }

    override fun onItemClick(equipment: Equipment) {
        // Вызов активности для редактирования снаряжения
        val intent = Intent(this, EditEquipmentActivity::class.java)
        intent.putExtra("equipmentId", equipment.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT_EQUIPMENT)
    }

    override fun onDeleteClick(equipment: Equipment) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Удаление всех записей в route_equipment и rent, связанных с данным снаряжением
                val routeEquipmentList = db.routeEquipmentDao().getRouteEquipmentForEquipment(equipment.id)
                routeEquipmentList.forEach { routeEquipment ->
                    db.rentDao().deleteByRouteEquipmentId(routeEquipment.id)
                    db.routeEquipmentDao().deleteByRouteEquipmentId(routeEquipment.id)
                }
                db.equipmentDao().deleteEquipment(equipment)
            }
            loadEquipment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_EQUIPMENT && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadEquipment()
            }
        } else if (requestCode == REQUEST_CODE_EDIT_EQUIPMENT && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadEquipment()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_EQUIPMENT = 1
        private const val REQUEST_CODE_EDIT_EQUIPMENT = 2
    }
}
