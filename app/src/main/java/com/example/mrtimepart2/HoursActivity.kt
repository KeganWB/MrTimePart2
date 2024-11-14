package com.example.mrtimepart2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HoursActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours)

        val minHoursEditText = findViewById<EditText>(R.id.etMinHours)
        val maxHoursEditText = findViewById<EditText>(R.id.etMaxHours)
        val submitButton = findViewById<Button>(R.id.btnSubmitHours)
        val fabBackHours = findViewById<FloatingActionButton>(R.id.fabBackHours)

        submitButton.setOnClickListener {
            val minHours = minHoursEditText.text.toString()
            val maxHours = maxHoursEditText.text.toString()

            if (minHours.isNotEmpty() && maxHours.isNotEmpty()) {

                Toast.makeText(this, "Min: $minHours, Max: $maxHours", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter both minimum and maximum hours", Toast.LENGTH_SHORT).show()
            }
        }
        fabBackHours.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}