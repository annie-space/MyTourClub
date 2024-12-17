package com.example.mytourclub.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Route
import com.example.mytourclub.until.Direction
import com.example.mytourclub.until.Difficulty
import com.example.mytourclub.until.Typing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddRouteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_route)

        db = AppDatabase.getDatabase(this@AddRouteActivity)

        val etName = findViewById<EditText>(R.id.etName)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val etDates = findViewById<EditText>(R.id.etDates)
        val etLength = findViewById<EditText>(R.id.etLength)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etPlaces = findViewById<EditText>(R.id.etPlaces)
        val etGuide1 = findViewById<EditText>(R.id.etGuide1)
        val etGuide2 = findViewById<EditText>(R.id.etGuide2)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etPhoto = findViewById<EditText>(R.id.etPhoto)
        val spDirection = findViewById<Spinner>(R.id.spDirection)
        val spDifficulty = findViewById<Spinner>(R.id.spDifficulty)
        val spType = findViewById<Spinner>(R.id.spType)
        val btnAddRoute = findViewById<Button>(R.id.btnAddRoute)

        val directions = Direction.values().map { it.name }
        val directionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, directions)
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDirection.adapter = directionAdapter

        val difficulties = Difficulty.values().map { it.name }
        val difficultyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficulties)
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDifficulty.adapter = difficultyAdapter

        val types = Typing.values().map { it.name }
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = typeAdapter

        btnAddRoute.setOnClickListener {
            val direction = Direction.valueOf(spDirection.selectedItem.toString())
            val difficulty = Difficulty.valueOf(spDifficulty.selectedItem.toString())
            val type = Typing.valueOf(spType.selectedItem.toString())
            val guide1FullName = etGuide1.text.toString()
            val guide2FullName = etGuide2.text.toString()
            val photoId = getResourceId(etPhoto.text.toString())

            lifecycleScope.launch {
                val guide1 = withContext(Dispatchers.IO) { db.guideDao().getGuideByName(guide1FullName) }
                val guide2 = withContext(Dispatchers.IO) { db.guideDao().getGuideByName(guide2FullName) }

                if (guide1 == null || guide2 == null) {
                    val notFoundGuide = when {
                        guide1 == null -> "Гид 1: $guide1FullName"
                        guide2 == null -> "Гид 2: $guide2FullName"
                        else -> "Гиды не найдены"
                    }
                    runOnUiThread {
                        Toast.makeText(this@AddRouteActivity, "$notFoundGuide не найден!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val date = try {
                        dateFormat.parse(etDates.text.toString())
                    } catch (e: Exception) {
                        null
                    }

                    if (date == null) {
                        runOnUiThread {
                            Toast.makeText(this@AddRouteActivity, "Неверный формат даты!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val route = Route(
                            name = etName.text.toString(),
                            direction = direction,
                            difficulty = difficulty,
                            duration = etDuration.text.toString().toInt(),
                            dates = date,
                            length = etLength.text.toString().toInt(),
                            photoResId = photoId,
                            description = etDescription.text.toString(),
                            places = etPlaces.text.toString().toInt(),
                            available_places = etPlaces.text.toString().toInt(),
                            type = type,
                            admin_id = 1,
                            guide1_id = guide1.id,
                            guide2_id = guide2.id,
                            price = etPrice.text.toString().toDouble()
                        )

                        val routeId = withContext(Dispatchers.IO) {
                            db.routeDao().insert(route).toInt()
                        }

                        // Запуск активности выбора снаряжения
                        val intent = Intent(this@AddRouteActivity, SelectGearActivity ::class.java).apply {
                            putExtra("route_id", routeId)
                        }
                        startActivity(intent)

                        setResult(Activity.RESULT_OK)
                        finish()  // Закрыть активити после добавления маршрута
                    }
                }
            }
        }
    }

    private fun getResourceId(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }
}
