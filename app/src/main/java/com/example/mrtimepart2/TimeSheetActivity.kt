package com.example.mrtimepart2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeSheetActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        val fabAdd = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fabAdd.bringToFront()
        fabAdd.setOnClickListener() {
            Toast.makeText(this@TimeSheetActivity, "Clicked", Toast.LENGTH_SHORT ).show()
            val timesheetFragment = AddTimeSheetActivity()
            timesheetFragment.show(supportFragmentManager, "timeSheetDialogueFragment")
        }
    }
}