package com.example.mytourclub.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userId = intent.getIntExtra("userId", -1)

        if (userId == -1) {
            Toast.makeText(this, "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etHealthGroup = findViewById<EditText>(R.id.etHealthGroup)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvDob = findViewById<TextView>(R.id.tvDob)
        val btnSaveProfile = findViewById<Button>(R.id.btnSaveProfile)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@EditProfileActivity)
            val user = db.userDao().getUserById(userId)

            user?.let {
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                tvName.text = "Имя: ${it.name}"
                tvDob.text = "Дата рождения: ${dateFormat.format(it.dob)}"
                etEmail.setText(it.email)
                etPhone.setText(it.phone)
                etHealthGroup.setText(it.healthGroup)
                etPassword.setText(it.password)
            }
        }

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etHealthGroup = findViewById<EditText>(R.id.etHealthGroup)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val email = etEmail.text.toString()
        val phone = etPhone.text.toString()
        val healthGroup = etHealthGroup.text.toString()
        val password = etPassword.text.toString()

        if (email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@EditProfileActivity)
            db.userDao().updateUser(userId, email, phone, healthGroup, password)
            Toast.makeText(this@EditProfileActivity, "Изменения сохранены", Toast.LENGTH_SHORT).show()

            // Устанавливаем результат и завершаем активность
            val resultIntent = Intent()
            resultIntent.putExtra("userId", userId)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
