package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mytourclub.R
import com.example.mytourclub.util.UserManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {

    private var selectedItemId: Int = R.id.navigation_user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Получение данных из UserManager
        val adminId = UserManager.userId
        val userRole = UserManager.userRole
        selectedItemId = intent.getIntExtra("selectedItemId", R.id.navigation_user)

        if (adminId == -1 || userRole.isNullOrEmpty()) {
            finish()
            return
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_user -> {
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("selectedItemId", R.id.navigation_profile)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        findViewById<TextView>(R.id.tvConfirmBookings).setOnClickListener {
            startActivity(Intent(this, ConfirmBookingsActivity::class.java))
        }

        findViewById<TextView>(R.id.tvManageEmployees).setOnClickListener {
            startActivity(Intent(this, ManageEmployeesActivity::class.java))
        }

        findViewById<TextView>(R.id.tvManageRoutes).setOnClickListener {
            startActivity(Intent(this, ManageRoutesActivity::class.java))
        }

        findViewById<TextView>(R.id.tvManageEquipment).setOnClickListener {
            startActivity(Intent(this, ManageEquipmentActivity::class.java))
        }

        findViewById<TextView>(R.id.tvManageUsers).setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }
    }
}
