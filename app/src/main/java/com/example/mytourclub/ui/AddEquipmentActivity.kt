package com.example.mytourclub.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Equipment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEquipmentActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_equipment)

        db = AppDatabase.getDatabase(this@AddEquipmentActivity)

        val etName = findViewById<EditText>(R.id.etName)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etPhoto = findViewById<EditText>(R.id.etPhoto)
        val etPriceUnit = findViewById<EditText>(R.id.etPriceUnit)
        val btnAddEquipment = findViewById<Button>(R.id.btnAddEquipment)

        btnAddEquipment.setOnClickListener {
            val photoId = getResourceId(etPhoto.text.toString())
            val quantity = etQuantity.text.toString().toInt()
            val equipment = Equipment(
                name = etName.text.toString(),
                quantity = quantity,
                description = etDescription.text.toString(),
                photo = photoId,
                priceUnit = etPriceUnit.text.toString().toDouble(),

            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.equipmentDao().insertEquipment(equipment)
                }
                setResult(Activity.RESULT_OK)
                finish()  // Закрыть активити после добавления снаряжения
            }
        }
    }

    private fun getResourceId(resourceName: String): Int {
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }
}
