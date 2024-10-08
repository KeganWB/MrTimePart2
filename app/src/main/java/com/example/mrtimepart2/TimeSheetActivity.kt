package com.example.mrtimepart2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.api.Distribution.BucketOptions.Linear
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimeSheetActivity : AppCompatActivity() {
    private lateinit var timesheetContainer: LinearLayout
    private val timesheetList = mutableListOf<TimeSheetData>() // List to hold timesheet data
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private lateinit var sortSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)
        // Deletes Saved preferences between Views ( COMMENT THIS OUT ALWAYS UNLESS FOR RESET)
        /*
        val sharedpreferencesss = getSharedPreferences("TimesheetPrefs",Context.MODE_PRIVATE)
        sharedpreferencesss.edit().clear().apply()
         */
        timesheetContainer = findViewById(R.id.timesheetContainer)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val fabBack = findViewById<FloatingActionButton>(R.id.backArrow)

        sharedPreferences = getSharedPreferences("TimesheetPrefs", Context.MODE_PRIVATE)

        // Fetch and set up the sort spinner
        sortSpinner = findViewById<Spinner>(R.id.spinnerSort)
        val categoriesFetch = retrieveCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesFetch)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        // Listener to sort the timesheet list based on selected category
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position) as String
                sortTimesheets(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: handle case when nothing is selected
            }
        }

        // Retrieve and display previous timesheets
        retrieveTimesheets()

        fabBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        fabAdd.setOnClickListener {
            val addTimeSheetFragment = AddTimeSheetActivity()
            addTimeSheetFragment.setOnTimesheetAddedListener(object : AddTimeSheetActivity.OnTimesheetAddedListener {
                override fun onTimesheetAdded(timeSheetData: TimeSheetData) {
                    // Add new timesheet to list and view only if it's not already present
                    if (!timesheetList.contains(timeSheetData)) {
                        timesheetList.add(timeSheetData) // Store timesheet in the list
                        addTimesheetToView(timeSheetData) // Add new timesheet to view
                        saveTimesheets() // Save updated list
                    }
                }
            })
            addTimeSheetFragment.show(supportFragmentManager, "TimesheetFragment")
        }
    }

    private fun addTimesheetToView(timeSheetData: TimeSheetData) {
        val inflater = LayoutInflater.from(this)
        val timesheetView = inflater.inflate(R.layout.timesheetprefab, timesheetContainer, false)

        val nameTextView = timesheetView.findViewById<TextView>(R.id.nameTextView)
        val startTimeTextView = timesheetView.findViewById<TextView>(R.id.startTimeTextView)
        val endTimeTextView = timesheetView.findViewById<TextView>(R.id.endTimeTextView)
        val startDateTextView = timesheetView.findViewById<TextView>(R.id.startDateTextView) // New field
        val endDateTextView = timesheetView.findViewById<TextView>(R.id.endDateTextView)
        val descriptionTextView = timesheetView.findViewById<TextView>(R.id.descriptionTextView)
        val categoryTextView = timesheetView.findViewById<TextView>(R.id.categoryTextView)
        val imageView = timesheetView.findViewById<ImageView>(R.id.imageView)

        // Populate the timesheet view with data
        nameTextView.text = timeSheetData.name
        startTimeTextView.text = timeSheetData.startTime
        endTimeTextView.text = timeSheetData.endTime
        startDateTextView.text = timeSheetData.startDate // Display the start date
        endDateTextView.text = timeSheetData.endDate
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

    private fun saveTimesheets() {
        val jsonString = gson.toJson(timesheetList)
        with(sharedPreferences.edit()) {
            putString("TIMESHEET_LIST", jsonString)
            apply()
        }
    }

    private fun retrieveTimesheets() {
        val jsonString = sharedPreferences.getString("TIMESHEET_LIST", null)
        if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<TimeSheetData>>() {}.type
            val savedTimesheets: MutableList<TimeSheetData> = gson.fromJson(jsonString, type)

            // Clear the current list to avoid duplication
            timesheetList.clear()
            timesheetList.addAll(savedTimesheets) // Add all saved timesheets

            // Add the timesheets to the view
            savedTimesheets.forEach { timesheet ->
                addTimesheetToView(timesheet)
            }
        }
    }

    private fun retrieveCategories(): List<String> {
        val categoryRet = sharedPreferences
        val jsonString = categoryRet.getString("CATEGORY_LIST", null)
        return if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            val categories = gson.fromJson<MutableList<String>>(jsonString, type)
            categories.apply { add(0, "All") } // Add "All" option at the beginning
        } else {
            listOf("All") // Default options if no categories are saved
        }
    }

    private fun sortTimesheets(category: String) {
        // Clear current views from the container
        timesheetContainer.removeAllViews()

        // Sort the timesheets based on the selected category
        val sortedList = if (category == "All") {
            // No sorting, just display as is
            timesheetList
        } else {
            // Sort timesheetList by the selected category
            timesheetList.filter { it.category == category }
        }
        val dateSortedList = sortedList.sortedBy { it.startDate }

        // Re-add sorted timesheets to the view
        sortedList.forEach { timesheet ->
            addTimesheetToView(timesheet)
        }
    }
}
