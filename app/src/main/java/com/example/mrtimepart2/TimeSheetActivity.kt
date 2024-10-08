package com.example.mrtimepart2

import android.app.DatePickerDialog
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetActivity : AppCompatActivity() {
    private lateinit var timesheetContainer: LinearLayout
    private val timesheetList = mutableListOf<TimeSheetData>() // List to hold timesheet data
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private lateinit var sortSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        timesheetContainer = findViewById(R.id.timesheetContainer)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val fabBack = findViewById<FloatingActionButton>(R.id.backArrow)

        sharedPreferences = getSharedPreferences("TimesheetPrefs", Context.MODE_PRIVATE)

        // Fetch and set up the sort spinner
        sortSpinner = findViewById(R.id.spinnerSort)
        val categoriesFetch = retrieveCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesFetch)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        // Listener to sort the timesheet list based on selected category
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position) as String
                when {
                    selectedCategory == "Sort By Date" -> showDateInputDialog() // Show date input dialog
                    selectedCategory == "Sort By Category and Date" -> showCategoryAndDateInputDialog() // Show category and date input dialog
                    else -> sortTimesheets(selectedCategory) // Sort by category as before
                }
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
        val categories = if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            gson.fromJson<MutableList<String>>(jsonString, type)
        } else {
            mutableListOf()
        }

        // Add options to the list, including the new "Sort By Date" option
        categories.add(0, "All") // Ensure "All" is the first option
        categories.add("Sort By Date") // Add the "Sort By Date" option
        categories.add("Sort By Category and Date") // Add the new option
        return categories
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
        dateSortedList.forEach { timesheet ->
            addTimesheetToView(timesheet)
        }
    }

    private fun showDateInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_input, null)
        val startDateInput = dialogView.findViewById<TextView>(R.id.tvStartDate)
        val endDateInput = dialogView.findViewById<TextView>(R.id.tvEndDate)

        startDateInput.setOnClickListener {
            openDatePicker { date ->
                startDateInput.text = date
            }
        }

        endDateInput.setOnClickListener {
            openDatePicker { date ->
                endDateInput.text = date
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("Filter") { dialog, which ->
                val startDate = startDateInput.text.toString()
                val endDate = endDateInput.text.toString()
                filterTimesheetsByDate(startDate, endDate)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCategoryAndDateInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_date_input, null)
        val startDateInput = dialogView.findViewById<TextView>(R.id.tvStartDate)
        val endDateInput = dialogView.findViewById<TextView>(R.id.tvEndDate)
        val categoryInput = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

        // Set up category spinner
        val categoriesFetch = retrieveCategories().filter { it != "Sort By Date" && it != "Sort By Category and Date" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesFetch)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryInput.adapter = adapter

        startDateInput.setOnClickListener {
            openDatePicker { date ->
                startDateInput.text = date
            }
        }

        endDateInput.setOnClickListener {
            openDatePicker { date ->
                endDateInput.text = date
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Select Category and Date Range")
            .setView(dialogView)
            .setPositiveButton("Filter") { dialog, which ->
                val selectedCategory = categoryInput.selectedItem.toString()
                val startDate = startDateInput.text.toString()
                val endDate = endDateInput.text.toString()
                filterTimesheetsByCategoryAndDate(selectedCategory, startDate, endDate)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            onDateSelected(selectedDate)
        }, year, month, day).show()
    }

    private fun filterTimesheetsByDate(startDate: String, endDate: String) {
        // Clear current views from the container
        timesheetContainer.removeAllViews()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = dateFormat.parse(startDate) ?: Date(Long.MIN_VALUE) // Use minimum date if parsing fails
        val end = dateFormat.parse(endDate) ?: Date(Long.MAX_VALUE) // Use maximum date if parsing fails

        // Filter timesheets based on the selected date range
        val filteredList = timesheetList.filter { timesheet ->
            val timesheetStartDate = dateFormat.parse(timesheet.startDate)
            timesheetStartDate?.let { it in start..end } ?: false
        }

        // Re-add filtered timesheets to the view
        filteredList.forEach { timesheet ->
            addTimesheetToView(timesheet)
        }
    }

    private fun filterTimesheetsByCategoryAndDate(selectedCategory: String, startDate: String, endDate: String) {
        // Clear current views from the container
        timesheetContainer.removeAllViews()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = dateFormat.parse(startDate) ?: Date(Long.MIN_VALUE)
        val end = dateFormat.parse(endDate) ?: Date(Long.MAX_VALUE)

        // Filter timesheets based on the selected category and date range
        val filteredList = timesheetList.filter { timesheet ->
            val timesheetStartDate = dateFormat.parse(timesheet.startDate)
            timesheet.category == selectedCategory && timesheetStartDate?.let { it in start..end } ?: false
        }

        // Re-add filtered timesheets to the view
        filteredList.forEach { timesheet ->
            addTimesheetToView(timesheet)
        }
    }
}
