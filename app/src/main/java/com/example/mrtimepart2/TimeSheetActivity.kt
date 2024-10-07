package com.example.mrtimepart2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimeSheetActivity: AppCompatActivity() {
    private lateinit var timesheetContainer: LinearLayout
    private val timesheetList = mutableListOf<TimeSheetData>() // List to hold timesheet data
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        timesheetContainer = findViewById(R.id.timesheetContainer)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val fabBack = findViewById<FloatingActionButton>(R.id.backArrow)

        timesheetContainer = findViewById(R.id.timesheetContainer)
        sharedPreferences = getSharedPreferences("TimesheetPrefs", Context.MODE_PRIVATE)

        retrieveTimesheets()


        fabBack.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        fabAdd.setOnClickListener {
            val addTimeSheetFragment = AddTimeSheetActivity()
            addTimeSheetFragment.setOnTimesheetAddedListener(object : AddTimeSheetActivity.OnTimesheetAddedListener {
                override fun onTimesheetAdded(timeSheetData: TimeSheetData) {
                    addTimesheetToView(timeSheetData) // Add new timesheet to view
                    saveTimesheets()
                }
            })
            addTimeSheetFragment.show(supportFragmentManager, "TimesheetFragment")
        }
    }

    private fun addTimesheetToView(timeSheetData: TimeSheetData) {
        timesheetList.add(timeSheetData) // Store timesheet in the list

        val inflater = LayoutInflater.from(this)
        val timesheetView = inflater.inflate(R.layout.timesheetprefab, timesheetContainer, false)

        val nameTextView = timesheetView.findViewById<TextView>(R.id.nameTextView)
        val startTimeTextView = timesheetView.findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = timesheetView.findViewById<TextView>(R.id.endTimeTextView)
        val descriptionTextView = timesheetView.findViewById<TextView>(R.id.descriptionTextView)
        val categoryTextView = timesheetView.findViewById<TextView>(R.id.categoryTextView)
        val imageView = timesheetView.findViewById<ImageView>(R.id.imageView)

        // Populate the timesheet view with data
        nameTextView.text = timeSheetData.name
        startTimeTextView.text = timeSheetData.startTime
        endTimeTextView.text = timeSheetData.endTime
        descriptionTextView.text = timeSheetData.description
        categoryTextView.text = timeSheetData.category

        // If an image was provided, display it
        timeSheetData.image?.let { imageByteArray ->
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
        }

        // Add the new timesheet view to the container
        timesheetContainer.addView(timesheetView)
    }
    // saves to gson
    private fun saveTimesheets() {
        val jsonString = gson.toJson(timesheetList)
        with(sharedPreferences.edit()) {
            putString("TIMESHEET_LIST", jsonString)
            apply()
        }
    }

    // Retrieves previous timesheets from shared preferences (local gson/ json)
    private fun retrieveTimesheets() {
        val jsonString = sharedPreferences.getString("TIMESHEET_LIST", null)
        if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<TimeSheetData>>() {}.type
            val savedTimesheets: MutableList<TimeSheetData> = gson.fromJson(jsonString, type)
            timesheetList.addAll(savedTimesheets)

            // Add the timesheets to the view
            savedTimesheets.forEach { timesheet ->
                addTimesheetToView(timesheet)
            }
        }
    }
}