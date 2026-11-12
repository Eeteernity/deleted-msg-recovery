package com.aayan.deletedmsgrecovery

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = MessageAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Live-updates automatically as the listener service inserts
        // or flags messages — no manual refresh needed.
        AppDatabase.getInstance(this).messageDao().getAll()
            .observe(this) { messages -> adapter.submitList(messages) }

        findViewById<android.widget.Button>(R.id.btnGrantAccess).setOnClickListener {
            // Notification access can't be requested via a runtime permission
            // dialog — it requires sending the user to this system settings
            // screen, where they must manually toggle it on for this app.
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }
}
