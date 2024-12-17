package com.example.mytourclub.ui

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.dao.UserDao
import com.example.mytourclub.model.User
import com.example.mytourclub.util.UserManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        val editTextFullName = findViewById<EditText>(R.id.editTextFullName)
        val editTextDOB = findViewById<EditText>(R.id.editTextDOB)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPhone = findViewById<EditText>(R.id.editTextPhone)
        val editTextHealthGroup = findViewById<EditText>(R.id.editTextHealthGroup)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val fullName = editTextFullName.text.toString()
            val dobString = editTextDOB.text.toString()
            val dob = convertStringToDate(dobString)
            val email = editTextEmail.text.toString()
            val phone = editTextPhone.text.toString()
            val healthGroup = editTextHealthGroup.text.toString()
            val password = editTextPassword.text.toString()
            val gender = determineGenderFromFullName(fullName)

            if (isInputValid(fullName, dobString, email, phone, healthGroup, password)) {
                val user = User(name = fullName, dob = dob!!, email = email, phone = phone, healthGroup = healthGroup, password = password, gender = gender)

                lifecycleScope.launch {
                    userDao.insertUser(user)
                    val newUser = userDao.getUser(email, password)
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Участник зарегистрирован!", Toast.LENGTH_SHORT).show()
                        saveUserToPreferences(newUser?.id ?: -1, "user")
                        navigateToMainActivity()
                    }
                }
            }
        }
    }

    private fun isInputValid(fullName: String, dobString: String, email: String, phone: String, healthGroup: String, password: String): Boolean {
        if (fullName.split(" ").size != 3) {
            Toast.makeText(this, "Имя должно состоять из трех слов", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!(phone.startsWith("+7") || phone.startsWith("8")) || phone.length != 11) {
            Toast.makeText(this, "Телефон должен начинаться с +7 или 8 и содержать 10 цифр", Toast.LENGTH_SHORT).show()
            return false
        }
        if (convertStringToDate(dobString) == null) {
            Toast.makeText(this, "Некорректная дата", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!(healthGroup in listOf("1", "2", "3", "4", "5"))) {
            Toast.makeText(this, "Группа здоровья может быть только 1, 2, 3, 4 или 5", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Пароль не может быть пустым", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveUserToPreferences(userId: Int, userRole: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("userId", userId)
        editor.putString("userRole", userRole)
        editor.apply()

        // Установка данных в UserManager
        UserManager.setUser(userId, userRole)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun convertStringToDate(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(dateString)
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
