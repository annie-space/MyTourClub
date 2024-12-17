package com.example.mytourclub.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.RouteEquipment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectGearActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var routeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_gear)

        db = AppDatabase.getDatabase(this@SelectGearActivity)
        routeId = intent.getIntExtra("route_id", 0)

        val layoutEquipmentList = findViewById<LinearLayout>(R.id.layoutEquipmentList)
        val btnSaveEquipment = findViewById<Button>(R.id.btnSaveEquipment)

        lifecycleScope.launch {
            val equipmentList = withContext(Dispatchers.IO) { db.equipmentDao().getAllEquipment() }

            equipmentList.forEach { equipment ->
                val equipmentItemLayout = LinearLayout(this@SelectGearActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(8, 8, 8, 8)
                }

                val checkBox = CheckBox(this@SelectGearActivity).apply {
                    tag = equipment.id // Сохраняем ID снаряжения в теге чекбокса
                }

                val textView = TextView(this@SelectGearActivity).apply {
                    text = "${equipment.name} - ${equipment.quantity}"
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        setMargins(16, 0, 16, 0)
                    }
                }

                val editText = EditText(this@SelectGearActivity).apply {
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f).apply {
                        setMargins(16, 0, 0, 0)
                    }
                }

                equipmentItemLayout.addView(checkBox)
                equipmentItemLayout.addView(textView)
                equipmentItemLayout.addView(editText)
                layoutEquipmentList.addView(equipmentItemLayout)
            }
        }

        btnSaveEquipment.setOnClickListener {
            lifecycleScope.launch {
                val selectedEquipment = mutableListOf<RouteEquipment>()

                for (i in 0 until layoutEquipmentList.childCount) {
                    val equipmentItemLayout = layoutEquipmentList.getChildAt(i) as LinearLayout
                    val checkBox = equipmentItemLayout.getChildAt(0) as CheckBox
                    val editText = equipmentItemLayout.getChildAt(2) as EditText

                    if (checkBox.isChecked) {
                        val quantity = editText.text.toString().toIntOrNull() ?: 1
                        val equipmentId = checkBox.tag as Int // Получаем ID снаряжения из тега
                        selectedEquipment.add(RouteEquipment(routeId = routeId, equipmentId = equipmentId, quantityRequired = quantity))
                    }
                }

                withContext(Dispatchers.IO) {
                    db.routeEquipmentDao().insertAll(selectedEquipment)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
