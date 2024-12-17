package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.dao.AdminDao
import com.example.mytourclub.dao.GuideDao
import com.example.mytourclub.dao.UserDao
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Admin
import com.example.mytourclub.model.Guide
import com.example.mytourclub.model.User
import com.example.mytourclub.util.UserManager
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var userDao: UserDao
    private lateinit var adminDao: AdminDao
    private lateinit var guideDao: GuideDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Инициализация базы данных и DAO
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        adminDao = db.adminDao()
        guideDao = db.guideDao()

        // Инициализация администратора и гидов
        initializeAdminAndGuides()

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonLogin.setOnClickListener {
            // Логика авторизации
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Проверка данных в базе данных
            lifecycleScope.launch {
                val user = checkUserInDatabase(email, password)
                val admin = adminDao.getAdminByEmailAndPassword(email, password)
                val guide = guideDao.getGuideByEmailAndPassword(email, password)

                when {
                    user != null -> {
                        saveUserToPreferences(user.id, "user")
                        navigateToMainActivity()
                    }
                    admin != null -> {
                        saveUserToPreferences(admin.id, "admin")
                        navigateToMainActivity()
                    }
                    guide != null -> {
                        saveUserToPreferences(guide.id, "guide")
                        navigateToMainActivity()
                    }
                    else -> {
                        runOnUiThread {
                            Toast.makeText(this@AuthActivity, "Неверные данные", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        buttonRegister.setOnClickListener {
            // Переход на экран регистрации
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
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
        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initializeAdminAndGuides() {
        lifecycleScope.launch {
            val adminName = "Admin"
            val admin = adminDao.getAdminByName(adminName)
            if (admin == null) {
                adminDao.insertAdmin(
                    Admin(
                        name = adminName,
                        phone = "+79011234567",
                        email = "admin@example.com",
                        password = "admin_password"
                    )
                )
            }

            val guide1Name = "Сергей Сергеевич Сергеев"
            val guide1 = guideDao.getGuideByName(guide1Name)
            if (guide1 == null) {
                guideDao.insertGuide(
                    Guide(
                        name = guide1Name,
                        email = "guide1@example.com",
                        phone = "+79011234570",
                        experience = 10,
                        specialization = "горы, вода",
                        password = "guide1_password",
                        gender = "Мужской"
                    )
                )
            }

            val guide2Name = "Мария Михайловна Маркова"
            val guide2 = guideDao.getGuideByName(guide2Name)
            if (guide2 == null) {
                guideDao.insertGuide(
                    Guide(
                        name = guide2Name,
                        email = "guide2@example.com",
                        phone = "+79011234571",
                        experience = 5,
                        specialization = "кони, горы",
                        password = "guide2_password",
                        gender = "Женский"
                    )
                )
            }

            val guide3Name = "Иван Иванович Иванов"
            val guide3 = guideDao.getGuideByName(guide3Name)
            if (guide3 == null) {
                guideDao.insertGuide(
                    Guide(
                        name = guide3Name,
                        email = "guide3@example.com",
                        phone = "+79011234572",
                        experience = 8,
                        specialization = "лес, рыбалка",
                        password = "guide3_password",
                        gender = "Мужской"
                    )
                )
            }
        }
    }

    private suspend fun checkUserInDatabase(email: String, password: String): User? {
        return userDao.getUser(email, password)
    }
}
