package com.example.mrtimepart2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mrtimepart2.databinding.ActivityTimeSheetBinding


class TimeSheet : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTimeSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Creates pop up of Dialogue_new_timesheet

        val fabAdd = binding.fabNew

        fabAdd.setOnClickListener {
            Log.d("Fab","Fab Clicked")
            // Displays timesheet fragment(pop-up)
            val timesheetFragment = AddTimeSheet()
            timesheetFragment.show(supportFragmentManager, "timesheetFragment")
        }
    }
}