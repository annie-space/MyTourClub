package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.EquipmentAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Booking
import com.example.mytourclub.model.Rent
import com.example.mytourclub.model.Route
import com.example.mytourclub.util.UserManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class EquipmentSelectionActivity : AppCompatActivity() {

    private lateinit var equipmentAdapter: EquipmentAdapter
    private var routeId: Int = -1
    private var bookingId: Int = -1
    private var totalCost: Double = 0.0
    private lateinit var rentalDate: Date
    private lateinit var returnDate: Date
    private lateinit var route: Route

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipment_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEquipment)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = AppDatabase.getDatabase(this)
        routeId = intent.getIntExtra("routeId", -1)

        // Проверка авторизации пользователя
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userId", -1)
        val userRole = sharedPreferences.getString("userRole", null)

        UserManager.setUser(userId, userRole)

        if (UserManager.userId == -1) {
            Log.e("EquipmentSelection", "Некорректный ID пользователя")
            Toast.makeText(this, "Некорректный ID пользователя", Toast.LENGTH_SHORT).show()
            return
        }

        if (routeId != -1) {
            Log.d("EquipmentSelection", "Received route ID: $routeId")

            lifecycleScope.launch {
                try {
                    route = db.routeDao().getRouteById(routeId)!!
                    rentalDate = route.dates
                    returnDate = Calendar.getInstance().apply {
                        time = rentalDate
                        add(Calendar.DAY_OF_MONTH, route.duration)
                    }.time

                    val routeEquipmentList = db.routeEquipmentDao().getEquipmentByRoute(routeId)
                    Log.d("EquipmentSelection", "Route Equipment List for Route ID $routeId: $routeEquipmentList")

                    if (routeEquipmentList.isNotEmpty()) {
                        val equipmentList = routeEquipmentList.mapNotNull {
                            val equipment = db.equipmentDao().getEquipmentById(it.equipmentId)
                            if (equipment != null) {
                                Log.d("EquipmentSelection", "Loaded equipment: ID=${it.equipmentId}, Name=${equipment.name}, Price=${equipment.priceUnit}")
                                equipment
                            } else {
                                Log.e("EquipmentSelection", "Failed to load equipment with ID: ${it.equipmentId}")
                                null
                            }
                        }

                        // Создание мапы рекомендованного количества
                        val recommendedQuantities = routeEquipmentList.associateBy({ it.equipmentId }, { it.quantityRequired })

                        if (equipmentList.isNotEmpty()) {
                            equipmentAdapter = EquipmentAdapter(equipmentList.toMutableList(), recommendedQuantities) { equipment, quantity, view ->
                                rentEquipment(equipment.id, quantity, view)
                            }
                            recyclerView.adapter = equipmentAdapter
                            Toast.makeText(this@EquipmentSelectionActivity, "Снаряжение загружено для маршрута ID $routeId", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@EquipmentSelectionActivity, "Не удалось загрузить соответствующие записи из таблицы Equipment", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@EquipmentSelectionActivity, "Нет снаряжения для этого маршрута", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("EquipmentSelection", "Error loading route equipment", e)
                    Toast.makeText(this@EquipmentSelectionActivity, "Ошибка при загрузке списка снаряжения", Toast.LENGTH_SHORT).show()
                }

                // Создание новой записи бронирования с учетом стоимости маршрута
                val booking = Booking(
                    userId = UserManager.userId,
                    routeId = routeId,
                    bookingDate = Date(),
                    totalPrice = route.price, // Учитываем стоимость маршрута
                    status = false
                )
                bookingId = db.bookingDao().insert(booking).toInt()
                totalCost = 0.0 // Инициализируем начальную стоимость аренды
            }
        } else {
            Log.e("EquipmentSelection", "Некорректный ID маршрута")
            Toast.makeText(this, "Некорректный ID маршрута", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnBook).setOnClickListener {
            finishBooking()
        }
    }

    private fun rentEquipment(equipmentId: Int, quantity: Int, view: View) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@EquipmentSelectionActivity)
            val equipment = db.equipmentDao().getEquipmentById(equipmentId)
            if (equipment != null) {
                val rentedQuantity = db.rentDao().getRentedQuantity(equipmentId, rentalDate, returnDate) ?: 0
                val availableQuantity = equipment.quantity - rentedQuantity

                if (quantity > availableQuantity) {
                    AlertDialog.Builder(this@EquipmentSelectionActivity)
                        .setTitle("Недостаточное количество")
                        .setMessage("${equipment.name} - $quantity (на складе $availableQuantity)")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    val routeEquipment = db.routeEquipmentDao().getRouteEquipmentForEquipmentAndRoute(equipment.id, routeId)
                    val rentId = db.rentDao().insert(Rent(
                        bookingId = bookingId,
                        routeEquipmentId = routeEquipment.id,
                        rentalDate = rentalDate,
                        returnDate = returnDate,
                        price = equipment.priceUnit * quantity,
                        quantity = quantity
                    )).toInt()

                    // Обновляем общую стоимость аренды
                    totalCost += equipment.priceUnit * quantity

                    // Прячем кнопку и показываем текст "Ваше"
                    view.visibility = View.GONE
                    val rentedText = (view.parent as View).findViewById<TextView>(R.id.tvRented)
                    rentedText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun finishBooking() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@EquipmentSelectionActivity)

            Toast.makeText(this@EquipmentSelectionActivity, "Бронирование завершено", Toast.LENGTH_SHORT).show()

            // Переход на RentSummaryActivity
            val intent = Intent(this@EquipmentSelectionActivity, RentSummaryActivity::class.java)
            intent.putExtra("totalCost", totalCost) // Передаем только стоимость аренды
            intent.putExtra("routeId", routeId)
            intent.putExtra("userId", UserManager.userId)
            intent.putExtra("bookingId", bookingId) // Передаем bookingId
            startActivity(intent)
        }
    }
}
