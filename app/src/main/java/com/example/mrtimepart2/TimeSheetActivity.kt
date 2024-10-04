package com.example.mrtimepart2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.api.Distribution.BucketOptions.Linear

class TimeSheetActivity: AppCompatActivity() {
    private lateinit var timesheetContainer: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)
        timesheetContainer = findViewById<LinearLayout>(R.id.timesheetContainer)

        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            val addTimeSheetFragment = AddTimeSheetActivity()
            addTimeSheetFragment.show(supportFragmentManager, "TimesheetFragment")
        }

        val timeSheetData = intent.getParcelableExtra<TimeSheetData>("timeSheetData")

        timeSheetData?.let {
            val inflater = LayoutInflater.from(this)
            val timesheetView = inflater.inflate(R.layout.timesheetprefab, timesheetContainer, false)

            val nameTextView = timesheetView.findViewById<TextView>(R.id.nameTextView)
            val startTimeTextView = timesheetView.findViewById<TextView>(R.id.startTimeTextView)
            val endTimeTextView = timesheetView.findViewById<TextView>(R.id.endTimeTextView)
            val descriptionTextView = timesheetView.findViewById<TextView>(R.id.descriptionTextView)
            val categoryTextView = timesheetView.findViewById<TextView>(R.id.categoryTextView)
            val imageView = timesheetView.findViewById<ImageView>(R.id.imageView)

            // Populate the timesheet view with data
            nameTextView.text = it.name
            startTimeTextView.text = it.startTime
            endTimeTextView.text = it.endTime
            descriptionTextView.text = it.description
            categoryTextView.text = it.category

            // If an image was provided, display it
            it.image?.let { imageByteArray ->
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                imageView.setImageBitmap(bitmap)
                imageView.visibility = View.VISIBLE
            }

            // Add the new timesheet view to the container
            timesheetContainer.addView(timesheetView)
        }
    }
}