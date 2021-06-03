package com.example.stopwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.stopwatch.stopwatch.StopWatch
import com.example.stopwatch.timers.TimersActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stopwatch : ImageButton = findViewById(R.id.btnstopwatch)
        val timers : ImageButton = findViewById(R.id.btntimer)

        stopwatch.setOnClickListener {
            val intent  = Intent(this, StopWatch::class.java)
            startActivity(intent)
            finish()
        }

        timers.setOnClickListener {
            val intent  = Intent(this, TimersActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}