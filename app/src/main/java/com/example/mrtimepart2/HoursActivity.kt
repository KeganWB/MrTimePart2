package com.example.mrtimepart2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class HoursActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hours)

        val userId = intent.getStringExtra("USER_ID") ?: return

        val minHoursEditText = findViewById<EditText>(R.id.etMinHours)
        val maxHoursEditText = findViewById<EditText>(R.id.etMaxHours)
        val submitButton = findViewById<Button>(R.id.btnSubmitHours)
        val fabBackHours = findViewById<FloatingActionButton>(R.id.fabBackHours)

        submitButton.setOnClickListener {
            val minHours = minHoursEditText.text.toString()
            val maxHours = maxHoursEditText.text.toString()

            if (minHours.isNotEmpty() && maxHours.isNotEmpty()) {
                saveOrUpdateHours(userId, minHours, maxHours)
            } else {
                Toast.makeText(this, "Please enter both minimum and maximum hours", Toast.LENGTH_SHORT).show()
            }
        }

        fabBackHours.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun saveOrUpdateHours(userId: String, minHours: String, maxHours: String) {
        userId?.let { uid ->
            val hoursData = mapOf(
                "hours" to mapOf(
                    "minHours" to minHours,
                    "maxHours" to maxHours
                )
            )

            firestore.collection("users").document(uid)
                .set(hoursData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Hours successfully saved!", Toast.LENGTH_SHORT).show()
                    Log.d("Firestore", "Hours successfully saved/updated!")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save hours: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.w("Firestore", "Error saving hours", e)
                }
        } ?: Log.w("Firestore", "User ID is null, cannot save hours.")
    }
}
