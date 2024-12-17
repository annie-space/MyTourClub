package com.example.mytourclub.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Guide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditGuideActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var guideId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_guide)

        db = AppDatabase.getDatabase(this)
        guideId = intent.getIntExtra("guideId", -1)

        if (guideId != -1) {
            lifecycleScope.launch {
                val guide = withContext(Dispatchers.IO) { db.guideDao().getGuideById(guideId) }
                guide?.let {
                    findViewById<EditText>(R.id.etEditName).setText(it.name)
                    findViewById<EditText>(R.id.etEditEmail).setText(it.email)
                    findViewById<EditText>(R.id.etEditPhone).setText(it.phone)
                    findViewById<EditText>(R.id.etEditExperience).setText(it.experience.toString())
                    findViewById<EditText>(R.id.etEditSpecialization).setText(it.specialization)
                    findViewById<EditText>(R.id.etEditPassword).setText(it.password)
                } ?: run {
                    Toast.makeText(this@EditGuideActivity, "Ошибка: Гид не найден", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Ошибка: Неверный ID гида", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnSaveGuide).setOnClickListener {
            saveGuide()
        }
    }

    private fun saveGuide() {
        val name = findViewById<EditText>(R.id.etEditName).text.toString()
        val email = findViewById<EditText>(R.id.etEditEmail).text.toString()
        val phone = findViewById<EditText>(R.id.etEditPhone).text.toString()
        val experience = findViewById<EditText>(R.id.etEditExperience).text.toString().toIntOrNull() ?: 0
        val specialization = findViewById<EditText>(R.id.etEditSpecialization).text.toString()
        val password = findViewById<EditText>(R.id.etEditPassword).text.toString()
        val gender = determineGenderFromFullName(name)

        if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && experience >= 0 && specialization.isNotEmpty() && password.isNotEmpty()) {
            val guide = Guide(id = guideId, name = name, email = email, phone = phone, experience = experience, specialization = specialization, password = password, gender = gender)

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.guideDao().update(guide)
                }
                setResult(RESULT_OK)
                finish()
            }
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
    }

    private fun determineGenderFromFullName(fullName: String): String {
        val nameParts = fullName.split(" ")
        if (nameParts.size == 3) {
            val patronymic = nameParts[2]
            return when {
                patronymic.endsWith("ич") -> "Мужской"
                patronymic.endsWith("на") -> "Женский"
                else -> "Неопределенный"
            }
        }
        return "Неопределенный"
    }
}
