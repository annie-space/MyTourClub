package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.GuideAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Guide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageEmployeesActivity : AppCompatActivity(), GuideAdapter.OnItemClickListener {

    private lateinit var guideAdapter: GuideAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_employees)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewGuides)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this@ManageEmployeesActivity)

        findViewById<Button>(R.id.btnAddGuide).setOnClickListener {
            val intent = Intent(this, AddGuideActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_GUIDE)
        }

        lifecycleScope.launch {
            loadGuides()
        }
    }

    private suspend fun loadGuides() {
        val guides = withContext(Dispatchers.IO) { db.guideDao().getAllGuides() }

        withContext(Dispatchers.Main) {
            guideAdapter = GuideAdapter(guides, this@ManageEmployeesActivity)
            findViewById<RecyclerView>(R.id.recyclerViewGuides).adapter = guideAdapter
        }
    }

    override fun onItemClick(guide: Guide) {
        val intent = Intent(this, EditGuideActivity::class.java)
        intent.putExtra("guideId", guide.id)
        startActivityForResult(intent, REQUEST_CODE_EDIT_GUIDE)
    }

    override fun onDeleteClick(guide: Guide) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val defaultGuide = db.guideDao().getGuideByName("Иван Иванович Иванов")
                if (defaultGuide != null) {
                    db.routeDao().updateGuide1(defaultGuide.id, guide.id)
                    db.routeDao().updateGuide2(defaultGuide.id, guide.id)
                    db.guideDao().deleteGuide(guide)
                }
            }
            loadGuides()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_GUIDE && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadGuides()
            }
        } else if (requestCode == REQUEST_CODE_EDIT_GUIDE && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                loadGuides()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_ADD_GUIDE = 1
        private const val REQUEST_CODE_EDIT_GUIDE = 2
    }
}
