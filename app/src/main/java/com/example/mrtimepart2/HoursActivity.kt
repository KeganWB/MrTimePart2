package com.example.mrtimepart2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class HoursActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours)

        // Find the EditTexts and Button by their IDs
        val minHoursEditText = findViewById<EditText>(R.id.etMinHours)
        val maxHoursEditText = findViewById<EditText>(R.id.etMaxHours)
        val submitButton = findViewById<Button>(R.id.btnSubmitHours)

        // Set up a click listener for the submit button
        submitButton.setOnClickListener {
            val minHours = minHoursEditText.text.toString()
            val maxHours = maxHoursEditText.text.toString()

            if (minHours.isNotEmpty() && maxHours.isNotEmpty()) {
                // You can use these values for any further logic
                // For now, just show a toast message with the entered hours
                Toast.makeText(this, "Min: $minHours, Max: $maxHours", Toast.LENGTH_SHORT).show()
            } else {
                // Show a message if the fields are empty
                Toast.makeText(this, "Please enter both minimum and maximum hours", Toast.LENGTH_SHORT).show()
            }
        }
    }
}