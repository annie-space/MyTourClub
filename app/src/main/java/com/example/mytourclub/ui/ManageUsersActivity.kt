package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.UserAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageUsersActivity : AppCompatActivity(), UserAdapter.OnItemClickListener {

    private lateinit var userAdapter: UserAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        db = AppDatabase.getDatabase(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            loadUsers()
        }
    }

    private suspend fun loadUsers() {
        val users = withContext(Dispatchers.IO) { db.userDao().getAllUsers() }

        withContext(Dispatchers.Main) {
            userAdapter = UserAdapter(users, this@ManageUsersActivity)
            findViewById<RecyclerView>(R.id.recyclerViewUsers).adapter = userAdapter
        }
    }

    override fun onItemClick(user: User) {

    }

    override fun onDeleteClick(user: User) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Удаление связанных записей перед удалением пользователя
                db.reviewDao().deleteByUserId(user.id)
                db.bookingDao().deleteByUserId(user.id)


                // Удаление пользователя
                db.userDao().deleteUser(user)
            }
            loadUsers()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_USER && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadUsers()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_EDIT_USER = 1
    }
}
