package com.example.mytourclub.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Guide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddGuideActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_guide)

        db = AppDatabase.getDatabase(this@AddGuideActivity)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etExperience = findViewById<EditText>(R.id.etExperience)
        val etSpecialization = findViewById<EditText>(R.id.etSpecialization)
        val etPassword = findViewById<EditText>(R.id.etPassword)  // Добавляем поле для пароля
        val btnAddGuide = findViewById<Button>(R.id.btnAddGuide)

        btnAddGuide.setOnClickListener {
            val gender = determineGenderFromFullName(etName.text.toString())
            val guide = Guide(
                name = etName.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString(),
                experience = etExperience.text.toString().toInt(),  // Преобразуем строку в Int
                specialization = etSpecialization.text.toString(),
                gender = gender,  // Автоматически определяем пол
                password = etPassword.text.toString()  // Добавляем значение для пароля
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.guideDao().insertGuide(guide)
                }
                setResult(Activity.RESULT_OK)
                finish()  // Закрыть активити после добавления гида
            }
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
