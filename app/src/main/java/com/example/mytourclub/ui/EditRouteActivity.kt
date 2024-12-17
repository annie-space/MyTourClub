package com.example.mytourclub.ui

import android.app.Activity
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

class EditRouteActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var routeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_route)

        db = AppDatabase.getDatabase(this)
        routeId = intent.getIntExtra("routeId", -1)

        val etName = findViewById<EditText>(R.id.etEditName)
        val etDuration = findViewById<EditText>(R.id.etEditDuration)
        val etDates = findViewById<EditText>(R.id.etEditDates)
        val etLength = findViewById<EditText>(R.id.etEditLength)
        val etDescription = findViewById<EditText>(R.id.etEditDescription)
        val etPlaces = findViewById<EditText>(R.id.etEditPlaces)
        val etGuide1 = findViewById<EditText>(R.id.etEditGuide1)
        val etGuide2 = findViewById<EditText>(R.id.etEditGuide2)
        val etPrice = findViewById<EditText>(R.id.etEditPrice)
        val etPhoto = findViewById<EditText>(R.id.etEditPhoto)
        val spDirection = findViewById<Spinner>(R.id.spEditDirection)
        val spDifficulty = findViewById<Spinner>(R.id.spEditDifficulty)
        val spType = findViewById<Spinner>(R.id.spEditType)
        val btnSaveRoute = findViewById<Button>(R.id.btnSaveRoute)

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

        if (routeId != -1) {
            lifecycleScope.launch {
                val route = withContext(Dispatchers.IO) { db.routeDao().getRouteById(routeId) }
                route?.let {
                    etName.setText(it.name)
                    spDirection.setSelection(directions.indexOf(it.direction.name))
                    spDifficulty.setSelection(difficulties.indexOf(it.difficulty.name))
                    spType.setSelection(types.indexOf(it.type.name))
                    etDuration.setText(it.duration.toString())
                    etDates.setText(dateFormat.format(it.dates))
                    etLength.setText(it.length.toString())
                    etDescription.setText(it.description)
                    etPlaces.setText(it.places.toString())
                    etGuide1.setText(it.guide1_id.toString())
                    etGuide2.setText(it.guide2_id.toString())
                    etPrice.setText(it.price.toString())
                    etPhoto.setText(it.photoResId?.toString() ?: "")
                } ?: run {
                    Toast.makeText(this@EditRouteActivity, "Ошибка: Маршрут не найден", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Ошибка: Неверный ID маршрута", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSaveRoute.setOnClickListener {
            saveRoute()
        }
    }

    private fun saveRoute() {
        val name = findViewById<EditText>(R.id.etEditName).text.toString()
        val direction = Direction.valueOf(findViewById<Spinner>(R.id.spEditDirection).selectedItem.toString())
        val difficulty = Difficulty.valueOf(findViewById<Spinner>(R.id.spEditDifficulty).selectedItem.toString())
        val type = Typing.valueOf(findViewById<Spinner>(R.id.spEditType).selectedItem.toString())
        val duration = findViewById<EditText>(R.id.etEditDuration).text.toString().toIntOrNull() ?: 0
        val dates = findViewById<EditText>(R.id.etEditDates).text.toString()
        val length = findViewById<EditText>(R.id.etEditLength).text.toString().toIntOrNull() ?: 0
        val description = findViewById<EditText>(R.id.etEditDescription).text.toString()
        val places = findViewById<EditText>(R.id.etEditPlaces).text.toString().toIntOrNull() ?: 0
        val guide1 = findViewById<EditText>(R.id.etEditGuide1).text.toString().toIntOrNull() ?: 0
        val guide2 = findViewById<EditText>(R.id.etEditGuide2).text.toString().toIntOrNull() ?: 0
        val price = findViewById<EditText>(R.id.etEditPrice).text.toString().toDoubleOrNull() ?: 0.0
        val photo = findViewById<EditText>(R.id.etEditPhoto).text.toString().toIntOrNull() ?: 0

        if (name.isNotEmpty() && duration > 0 && dates.isNotEmpty() && length > 0 && description.isNotEmpty() && places > 0 && guide1 > 0 && guide2 > 0 && price > 0) {
            val date = try {
                dateFormat.parse(dates)
            } catch (e: Exception) {
                null
            }

            if (date == null) {
                Toast.makeText(this, "Неверный формат даты!", Toast.LENGTH_SHORT).show()
            } else {
                val route = Route(
                    id = routeId,
                    name = name,
                    direction = direction,
                    difficulty = difficulty,
                    duration = duration,
                    dates = date,
                    length = length,
                    description = description,
                    places = places,
                    available_places = places,
                    type = type,
                    admin_id = 1, // Укажите правильное значение admin_id
                    guide1_id = guide1,
                    guide2_id = guide2,
                    price = price,
                    photoResId = photo
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.routeDao().update(route)
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getResourceId(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }
}
