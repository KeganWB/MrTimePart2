package com.example.mrtimepart2

import TimeSheetData
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TimeSheetActivity : AppCompatActivity() {
    private lateinit var timesheetContainer: LinearLayout
    private val timesheetList = mutableListOf<TimeSheetData>()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private lateinit var sortSpinner: Spinner
    private lateinit var totalHoursTextView: TextView
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

        val userId = intent.getStringExtra("USER_ID") ?: return

        firestore = FirebaseFirestore.getInstance()
        Log.d("AddTimeSheetActivity", "Retrieving timesheet data")
        firestore.collection("timesheets").get()
            .addOnSuccessListener { result ->
                Log.d("AddTimeSheetActivity", "Timesheets retrieved: ${result.size()}")
            }
            .addOnFailureListener { exception ->
                Log.e("AddTimeSheetActivity", "Error retrieving timesheets", exception)
            }

        timesheetContainer = findViewById(R.id.timesheetContainer)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        val fabBack = findViewById<FloatingActionButton>(R.id.backArrow)
        totalHoursTextView = findViewById(R.id.textViewTotalHours)

        sharedPreferences = getSharedPreferences("TimesheetPrefs", Context.MODE_PRIVATE)

        sortSpinner = findViewById(R.id.spinnerSort)
        val categoriesFetch = retrieveCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesFetch)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position) as String
                when {
                    selectedCategory == "Sort By Date" -> showDateInputDialog()
                    selectedCategory == "Sort By Category and Date" -> showCategoryAndDateInputDialog()
                    else -> sortTimesheets(selectedCategory)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        retrieveTimesheetsFromFirestore()

        fabBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        fabAdd.setOnClickListener {
            val addTimeSheetFragment = AddTimeSheetActivity()
            addTimeSheetFragment.setOnTimesheetAddedListener(object : AddTimeSheetActivity.OnTimesheetAddedListener {
                override fun onTimesheetAdded(timeSheetData: TimeSheetData) {
                    if (!timesheetList.contains(timeSheetData)) {
                        timesheetList.add(timeSheetData)
                        addTimesheetToView(timeSheetData)
                        saveTimesheetsToFirestore(userId)
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

        nameTextView.text = timeSheetData.name
        startTimeTextView.text = timeSheetData.startTime
        endTimeTextView.text = timeSheetData.endTime
        startDateTextView.text = timeSheetData.startDate
        endDateTextView.text = timeSheetData.endDate
        descriptionTextView.text = timeSheetData.description
        categoryTextView.text = timeSheetData.category

        timeSheetData.image?.let { imageByteArray ->
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
        }

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

            timesheetList.clear()
            timesheetList.addAll(savedTimesheets)

            savedTimesheets.forEach { timesheet -> addTimesheetToView(timesheet) }
        }
    }

    private fun retrieveCategories(): List<String> {
        val jsonString = sharedPreferences.getString("CATEGORY_LIST", null)
        val categories = if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            gson.fromJson<MutableList<String>>(jsonString, type) ?: mutableListOf()
        } else {
            mutableListOf()
        }

        return listOf("All") + categories + listOf("Sort By Date", "Sort By Category and Date")
    }

    private fun sortTimesheets(category: String) {
        timesheetContainer.removeAllViews()
        totalHoursTextView.visibility = View.VISIBLE

        val filteredList = when (category) {
            "All" -> timesheetList
            else -> timesheetList.filter { it.category == category }
        }.sortedBy { it.startDate }

        val totalHours = calculateTotalHours(filteredList)
        totalHoursTextView.text = "Total Hours: $totalHours"

        filteredList.forEach { timesheet -> addTimesheetToView(timesheet) }
    }

    private fun showDateInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_input, null)
        val startDateInput = dialogView.findViewById<TextView>(R.id.tvStartDate)
        val endDateInput = dialogView.findViewById<TextView>(R.id.tvEndDate)

        startDateInput.setOnClickListener {
            openDatePicker { date -> startDateInput.text = date }
        }

        endDateInput.setOnClickListener {
            openDatePicker { date -> endDateInput.text = date }
        }

        AlertDialog.Builder(this)
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("Filter") { dialog, which ->
                val startDate = startDateInput.text.toString()
                val endDate = endDateInput.text.toString()
                if (isValidDateRange(startDate, endDate)) {
                    filterTimesheetsByDate(startDate, endDate)
                } else {
                    showError("Invalid date range!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCategoryAndDateInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_date_input, null)
        val startDateInput = dialogView.findViewById<TextView>(R.id.tvStartDate)
        val endDateInput = dialogView.findViewById<TextView>(R.id.tvEndDate)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

        val categories = retrieveCategories().filter { it != "All" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        startDateInput.setOnClickListener {
            openDatePicker { date -> startDateInput.text = date }
        }

        endDateInput.setOnClickListener {
            openDatePicker { date -> endDateInput.text = date }
        }

        AlertDialog.Builder(this)
            .setTitle("Filter By Category and Date Range")
            .setView(dialogView)
            .setPositiveButton("Filter") { dialog, which ->
                val startDate = startDateInput.text.toString()
                val endDate = endDateInput.text.toString()
                val selectedCategory = categorySpinner.selectedItem.toString()
                if (isValidDateRange(startDate, endDate)) {
                    filterTimesheetsByCategoryAndDate(selectedCategory, startDate, endDate)
                } else {
                    showError("Invalid date range!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun filterTimesheetsByDate(startDate: String, endDate: String) {
        timesheetContainer.removeAllViews()
        totalHoursTextView.visibility = View.VISIBLE

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val filteredList = timesheetList.filter {
            try {
                val timesheetStartDate = dateFormat.parse(it.startDate)
                val timesheetEndDate = dateFormat.parse(it.endDate)
                val startDateObj = dateFormat.parse(startDate)
                val endDateObj = dateFormat.parse(endDate)

                timesheetStartDate != null && timesheetEndDate != null &&
                        !timesheetStartDate.before(startDateObj) && !timesheetEndDate.after(endDateObj)
            } catch (e: Exception) {
                false
            }
        }
        val totalHours = calculateTotalHours(filteredList)

        totalHoursTextView.text = "Total Hours: $totalHours"

        filteredList.forEach { timesheet -> addTimesheetToView(timesheet) }
    }

    private fun filterTimesheetsByCategoryAndDate(category: String, startDate: String, endDate: String) {
        timesheetContainer.removeAllViews()
        totalHoursTextView.visibility = View.VISIBLE

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val filteredList = timesheetList.filter {
            try {
                val timesheetStartDate = dateFormat.parse(it.startDate)
                val timesheetEndDate = dateFormat.parse(it.endDate)
                val startDateObj = dateFormat.parse(startDate)
                val endDateObj = dateFormat.parse(endDate)

                timesheetStartDate != null && timesheetEndDate != null &&
                        !timesheetStartDate.before(startDateObj) && !timesheetEndDate.after(endDateObj)
            } catch (e: Exception) {
                false
            }
        }
        val totalHours = calculateTotalHours(filteredList)


        totalHoursTextView.text = "Total Hours: $totalHours"

        filteredList.forEach { timesheet -> addTimesheetToView(timesheet) }
    }

    private fun isValidDateRange(startDate: String, endDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)
            start != null && end != null && !start.after(end)
        } catch (e: Exception) {
            false
        }
    }

    private fun openDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun calculateTotalHours(timesheets: List<TimeSheetData>): Int {
        return timesheets.sumBy { it.calculateHours().toInt() }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun     saveTimesheetsToFirestore(userId: String) {
        val userTimesheetsCollection = firestore.collection("users").document(userId).collection("timesheets")

        // Clear existing data for this user if needed
        userTimesheetsCollection.get().addOnSuccessListener { snapshot ->
            for (doc in snapshot.documents) {
                doc.reference.delete()
            }

            timesheetList.forEach { timeSheetData ->
                userTimesheetsCollection.add(timeSheetData)
            }
        }.addOnFailureListener { e ->
            showError("Failed to save timesheets: ${e.message}")
        }
    }

    private fun retrieveTimesheetsFromFirestore() {
        val userId = intent.getStringExtra("USER_ID") ?: return

        firestore.collection("users").document(userId).collection("timesheets")
            .get()
            .addOnSuccessListener { snapshot ->
                timesheetList.clear()
                for (document in snapshot) {
                    val timesheet = document.toObject<TimeSheetData>()
                    timesheetList.add(timesheet)
                    addTimesheetToView(timesheet)
                }
            }
            .addOnFailureListener { e ->
                showError("Failed to retrieve timesheets: ${e.message}")
            }
    }
}
