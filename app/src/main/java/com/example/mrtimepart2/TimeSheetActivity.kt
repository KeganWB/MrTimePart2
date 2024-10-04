package com.example.mrtimepart2

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeSheetActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            val addTimeSheetFragment = AddTimeSheetActivity()
            addTimeSheetFragment.show(supportFragmentManager, "TimesheetFragment")
        }

        val timeSheetData = intent.getParcelableExtra<TimeSheetData>("timeSheetData")

        timeSheetData?.let {
            // Populate the fields in TimesheetActivity with the data
            /*
            nameTextView.text = it.name
            startTimeTextView.text = it.startTime
            endTimeTextView.text = it.endTime
            descriptionTextView.text = it.description
            categoryTextView.text = it.category



            // If an image was provided, display it in an ImageView
            it.image?.let { imageByteArray ->
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                imageView.setImageBitmap(bitmap) // Show the image in an ImageView

             */
        }

    }
}