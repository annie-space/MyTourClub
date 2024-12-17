package com.example.mytourclub.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Equipment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditEquipmentActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var equipmentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_equipment)

        db = AppDatabase.getDatabase(this)
        equipmentId = intent.getIntExtra("equipmentId", -1)

        if (equipmentId != -1) {
            lifecycleScope.launch {
                val equipment = withContext(Dispatchers.IO) { db.equipmentDao().getEquipmentById(equipmentId) }
                if (equipment != null) {
                    findViewById<EditText>(R.id.etEditName).setText(equipment.name)
                    findViewById<EditText>(R.id.etEditQuantity).setText(equipment.quantity.toString())
                    findViewById<EditText>(R.id.etEditDescription).setText(equipment.description)
                    findViewById<EditText>(R.id.etEditPhoto).setText(equipment.photo.toString())
                    findViewById<EditText>(R.id.etEditPriceUnit).setText(equipment.priceUnit.toString())

                } else {
                    Toast.makeText(this@EditEquipmentActivity, "Ошибка: Снаряжение не найдено", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Ошибка: Неверный ID снаряжения", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnSaveEquipment).setOnClickListener {
            saveEquipment()
        }
    }

    private fun saveEquipment() {
        val name = findViewById<EditText>(R.id.etEditName).text.toString()
        val quantity = findViewById<EditText>(R.id.etEditQuantity).text.toString().toIntOrNull() ?: 0
        val description = findViewById<EditText>(R.id.etEditDescription).text.toString()
        val photoId = getResourceId(findViewById<EditText>(R.id.etEditPhoto).text.toString())
        val price = findViewById<EditText>(R.id.etEditPriceUnit).text.toString().toDoubleOrNull()


        if (name.isNotEmpty() && price != null) {
            val equipment = Equipment(
                id = equipmentId,
                name = name,
                quantity = quantity,
                description = description,
                photo = photoId,
                priceUnit = price
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.equipmentDao().update(equipment)
                }
                setResult(RESULT_OK)
                finish()
            }
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getResourceId(resourceName: String): Int {
        val context = applicationContext
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}
