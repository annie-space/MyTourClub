package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mytourclub.R
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Admin
import com.example.mytourclub.model.Guide
import com.example.mytourclub.model.User
import com.example.mytourclub.util.UserManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private var userId: Int = -1
    private var selectedItemId: Int = R.id.navigation_profile
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Получение данных из UserManager
        userId = UserManager.userId
        userRole = UserManager.userRole
        selectedItemId = intent.getIntExtra("selectedItemId", R.id.navigation_profile)

        if (userId == -1 || userRole.isNullOrEmpty()) {
            Log.e("ProfileActivity", "Не удалось получить userId или userRole из UserManager")
            finish()
            return
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.putExtra("selectedItemId", R.id.navigation_home)
                    intent.putExtra("userRole", userRole)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish() // Завершаем текущую активность, чтобы перезагрузить
                    true
                }
                R.id.navigation_user -> {
                    when (userRole) {
                        "user" -> {
                            val intent = Intent(this, UserActivity::class.java)
                            intent.putExtra("userId", userId)
                            intent.putExtra("userRole", userRole)
                            intent.putExtra("selectedItemId", R.id.navigation_user)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        }

                        "admin" -> {
                            val intent = Intent(this, AdminActivity::class.java)
                            intent.putExtra("userId", userId)
                            intent.putExtra("userRole", userRole)
                            intent.putExtra("selectedItemId", R.id.navigation_user)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        }

                        "guide" -> {
                            val intent = Intent(this, GuideActivity::class.java)
                            intent.putExtra("userId", userId)
                            intent.putExtra("userRole", userRole)
                            intent.putExtra("selectedItemId", R.id.navigation_user)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        }
                    }
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@ProfileActivity)

            when (userRole) {
                "user" -> {
                    val user = db.userDao().getUserById(userId)
                    user?.let { populateUserProfile(it) }
                }
                "admin" -> {
                    val admin = db.adminDao().getAdminById(userId)
                    admin?.let { populateAdminProfile(it) }
                }
                "guide" -> {
                    val guide = db.guideDao().getGuideById(userId)
                    guide?.let { populateGuideProfile(it) }
                }
                else -> {
                    Log.e("ProfileActivity", "Unknown user role: $userRole")
                }
            }
        }

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        if (userRole == "user") {
            btnEditProfile.visibility = View.VISIBLE
            btnEditProfile.setOnClickListener {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("userId", userId)
                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
            }
        } else {
            btnEditProfile.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@ProfileActivity)
                val user = db.userDao().getUserById(userId)
                user?.let { populateUserProfile(it) }
            }
        }
    }

    private fun populateUserProfile(user: User) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(user.dob)

        findViewById<TextView>(R.id.tvName).text = "Имя: ${user.name}"
        findViewById<TextView>(R.id.tvEmail).text = "Email: ${user.email}"
        findViewById<TextView>(R.id.tvPhone).text = "Телефон: ${user.phone}"
        findViewById<TextView>(R.id.tvHealthGroup).apply {
            text = "Группа здоровья: ${user.healthGroup}"
            visibility = View.VISIBLE
        }
        findViewById<TextView>(R.id.tvDob).apply {
            text = "Дата рождения: $formattedDate"
            visibility = View.VISIBLE
        }
        findViewById<TextView>(R.id.tvGender).apply {
            text = "Пол: ${user.gender}"
            visibility = View.VISIBLE
        }
    }

    private fun populateAdminProfile(admin: Admin) {
        findViewById<TextView>(R.id.tvName).text = "Имя: ${admin.name}"
        findViewById<TextView>(R.id.tvEmail).text = "Email: ${admin.email}"
        findViewById<TextView>(R.id.tvPhone).text = "Телефон: ${admin.phone}"
    }

    private fun populateGuideProfile(guide: Guide) {
        findViewById<TextView>(R.id.tvName).text = "Имя: ${guide.name}"
        findViewById<TextView>(R.id.tvEmail).text = "Email: ${guide.email}"
        findViewById<TextView>(R.id.tvPhone).text = "Телефон: ${guide.phone}"
        findViewById<TextView>(R.id.tvExperience).apply {
            text = "Опыт: ${guide.experience} лет"
            visibility = View.VISIBLE
        }
        findViewById<TextView>(R.id.tvSpecialization).apply {
            text = "Специализация: ${guide.specialization}"
            visibility = View.VISIBLE
        }
        findViewById<TextView>(R.id.tvGender).apply {
            text = "Пол: ${guide.gender}"
            visibility = View.VISIBLE
        }
    }

    companion object {
        private const val REQUEST_CODE_EDIT_PROFILE = 1
    }
}
