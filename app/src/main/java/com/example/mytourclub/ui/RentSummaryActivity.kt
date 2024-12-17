package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import kotlinx.coroutines.launch

class RentSummaryActivity : AppCompatActivity() {

    private var userId: Int = -1 // ID пользователя
    private var routeId: Int = -1 // ID маршрута
    private var totalCost: Double = 0.0 // Итоговая стоимость аренды
    private var bookingId: Int = -1 // ID бронирования

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_summary)

        totalCost = intent.getDoubleExtra("totalCost", 0.0)
        routeId = intent.getIntExtra("routeId", -1)
        userId = intent.getIntExtra("userId", -1)
        bookingId = intent.getIntExtra("bookingId", -1)

        val db = AppDatabase.getDatabase(this)

        // Получение данных о маршруте
        lifecycleScope.launch {
            try {
                val route = db.routeDao().getRouteById(routeId)
                if (route == null) {
                    Log.e("RentSummaryActivity", "Маршрут с ID $routeId не найден")
                    Toast.makeText(this@RentSummaryActivity, "Маршрут не найден", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val routePrice = route.price

                // Вывод итоговой информации
                val totalPrice = routePrice + totalCost
                findViewById<TextView>(R.id.tvRouteName).text = route.name
                findViewById<TextView>(R.id.tvRoutePrice).text = "$routePrice руб."
                findViewById<TextView>(R.id.tvRentalCost).text = "$totalCost руб."
                findViewById<TextView>(R.id.tvTotalCost).text = "Итоговая сумма: $totalPrice руб."

                findViewById<Button>(R.id.btnPay).setOnClickListener {
                    lifecycleScope.launch {
                        try {
                            // Обновление стоимости бронирования в базе данных
                            val booking = db.bookingDao().getBookingById(bookingId)
                            if (booking != null) {
                                booking.totalPrice = totalPrice // Изменяем значение totalPrice
                                db.bookingDao().updateBooking(booking)
                            }

                            // Уменьшение available_places на 1
                            db.routeDao().updateAvailablePlaces(routeId, route.available_places - 1)

                            Toast.makeText(this@RentSummaryActivity, "Ваша заявка будет утверждена в ближайшее время", Toast.LENGTH_LONG).show()

                            // Переход на MainActivity
                            val intent = Intent(this@RentSummaryActivity, MainActivity::class.java)
                            intent.putExtra("userId", userId) // Возвращаем ID пользователя
                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("RentSummaryActivity", "Ошибка при обновлении записи в таблице Route", e)
                            Toast.makeText(this@RentSummaryActivity, "Ошибка при обновлении записи в таблице Route", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RentSummaryActivity", "Ошибка при загрузке данных маршрута", e)
                Toast.makeText(this@RentSummaryActivity, "Ошибка при загрузке данных маршрута", Toast.LENGTH_LONG).show()
            }
        }
    }
}
