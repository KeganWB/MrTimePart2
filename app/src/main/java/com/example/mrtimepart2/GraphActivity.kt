package com.example.mrtimepart2

import TimeSheetData
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mrtimepart2.databinding.ActivityGraphsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphsBinding
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
    private val timesheetList = mutableListOf<TimeSheetData>()
    private var minHours : Float = 0f
    private var maxHours: Float = 0f
    private var totalHours: Float = 0f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("USER_ID") ?: return
        fetchTimesheets(userId) {
            // Fetch "This Month" data once timesheets are loaded
            fetchDataForSelectedPeriod("This Month")
        }
        fetchHours(userId)//Fetches the min and Max hours for the program

        // Setup spinner
        val timePeriods = listOf("This Month", "Last Month", "Custom Range")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timePeriods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timePeriodSpinner.adapter = adapter

        binding.timePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPeriod = timePeriods[position]
                fetchDataForSelectedPeriod(selectedPeriod)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchTimesheets(userId: String, onComplete: () -> Unit) {
        db.collection("users").document(userId).collection("timesheets")
            .get()
            .addOnSuccessListener { snapshot ->
                timesheetList.clear()
                for (document in snapshot) {
                    val timesheet = document.toObject<TimeSheetData>()
                    timesheetList.add(timesheet)
                }
                onComplete()  // Call onComplete after data is loaded
            }
            .addOnFailureListener { e ->
                showError("Failed to retrieve timesheets: ${e.message}")
                onComplete() // Ensure to call onComplete even on failure
            }
    }
    private fun fetchHours(userId: String)
    {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val hoursData = document.get("hours") as? Map<*, *>
                     minHours = (hoursData?.get("minHours") as? String)?.toFloatOrNull() ?: 0f
                     maxHours = (hoursData?.get("maxHours") as? String)?.toFloatOrNull() ?: 0f
                }
            }
    }


    private fun fetchDataForSelectedPeriod(selectedPeriod: String) {
        val userId = intent.getStringExtra("USER_ID") ?: return
        val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        when (selectedPeriod) {
            "This Month" -> {
                startDate.set(Calendar.DAY_OF_MONTH, 1)
                endDate.time = calendar.time
            }
            "Last Month" -> {
                startDate.add(Calendar.MONTH, -1)
                startDate.set(Calendar.DAY_OF_MONTH, 1)
                endDate.time = startDate.time
                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            "Custom Range" -> {
                showDateRangePicker(userId)
                return
            }
        }

        queryTimesheetData(userId, dateFormat.format(startDate.time), dateFormat.format(endDate.time))
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    //Adds Timesheets from firestore to mutable list of timesheets
    private fun queryTimesheetData(userId: String, startDate: String, endDate: String) {
        val start = convertStringToDate(startDate)
        val end = convertStringToDate(endDate)
        totalHours = 0f
        if (start == null || end == null) {
            showError("Invalid date range")
            return
        }

        val filteredTimesheets = timesheetList.filter {
            val timesheetStart = convertStringToDate(it.startDate)
            val timesheetEnd = convertStringToDate(it.endDate)

            // Filter timesheets within the selected date range
            timesheetStart != null && timesheetEnd != null &&
                    timesheetStart >= start && timesheetEnd <= end
        }

        val hoursWorked = mutableMapOf<String, Float>()

        for (timesheet in filteredTimesheets) {
            val hours = timesheet.calculateHours().toFloat()
            if (hours > 0) {
                hoursWorked[timesheet.category] = hoursWorked.getOrDefault(timesheet.category, 0f) + hours
                totalHours += hoursWorked.getOrDefault(timesheet.category, 0f) + hours
            }
        }

        updateBarChart(totalHours,minHours,maxHours,hoursWorked)
    }

    private fun convertStringToDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: ParseException) {
            Log.e("GraphActivity", "Date parsing failed: $dateStr", e)
            null
        }
    }

    private fun updateBarChart(totalHours:Float, minHours: Float, maxHours: Float, hoursWorked: Map<String, Float>) {
        if (hoursWorked.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No data available for the selected period")
            return
        }

        val entries = listOf(
            BarEntry(1f, maxHours),
            BarEntry(2f, totalHours),
            BarEntry(3f, minHours)
        )
        val labels = listOf("Max Hours", "Total Hours", "Min Hours" )
        val dataset = BarDataSet(entries, "Hours data").apply {
            colors = listOf(Color.BLUE, Color.GREEN, Color.RED)
            valueTextSize = 12f
        }
        val barData = BarData(dataset).apply{
            barWidth = 0.4f
        }
        val xAxis = binding.barChart.xAxis
        xAxis.granularity = 1f
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

        // Use a ValueFormatter for the XAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels.getOrNull(value.toInt() - 1) ?: value.toString()
            }
        }

        // Set data to BarChart
        binding.barChart.data = barData
        binding.barChart.description.text = "Bar graph view of hours worked versus goals"
        binding.barChart.invalidate() // Refresh the chart
    }

    private fun showDateRangePicker(userId: String) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                val startDate = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDay)
                }
                val endDate = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay)
                }

                queryTimesheetData(userId, dateFormat.format(startDate.time), dateFormat.format(endDate.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}
