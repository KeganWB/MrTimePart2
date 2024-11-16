package com.example.mrtimepart2

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.mrtimepart2.databinding.ActivityGraphsBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the time period spinner
        val timePeriods = listOf("This Month", "Last Month", "Custom Range")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, timePeriods)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.timePeriodSpinner.adapter = adapter

        binding.timePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPeriod = timePeriods[position]
                fetchDataForSelectedPeriod(selectedPeriod)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }
    }

    private fun fetchDataForSelectedPeriod(selectedPeriod: String) {
        val userId = intent.getStringExtra("USER_ID") ?: return  // Get the logged-in user ID dynamically

        // Get the current date to filter based on the selected period
        val currentDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        when (selectedPeriod) {
            "This Month" -> {
                startDate.set(Calendar.DAY_OF_MONTH, 1)
                startDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH))
                endDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            "Last Month" -> {
                startDate.set(Calendar.DAY_OF_MONTH, 1)
                startDate.set(Calendar.MONTH, currentDate.get(Calendar.MONTH) - 1)
                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            "Custom Range" -> {
                showDateRangePicker(userId)
                return // Don't proceed further if custom range is selected
            }
        }

        // Query Firestore to get the timesheet data for the selected period
        queryTimesheetData(userId, startDate.time, endDate.time)
    }

    private fun queryTimesheetData(userId: String, startDate: Date, endDate: Date) {
        val timesheetRef = db.collection("users").document(userId).collection("timesheets")
        timesheetRef
            .whereGreaterThanOrEqualTo("workDate", startDate)
            .whereLessThanOrEqualTo("workDate", endDate)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val hoursWorkedMap = mutableMapOf<String, Float>()
                    querySnapshot.forEach { document ->
                        val workDate = document.getDate("workDate") ?: return@forEach
                        val workedHours = document.getDouble("workedHours")?.toFloat() ?: 0f

                        // Use workDate to categorize into days/months etc. (depends on selected period)
                        val periodKey = getPeriodKey(workDate, startDate, endDate)
                        hoursWorkedMap[periodKey] = (hoursWorkedMap[periodKey] ?: 0f) + workedHours
                    }
                    updatePieChart(hoursWorkedMap)
                }
            }
            .addOnFailureListener { e ->
                Log.w("GraphActivity", "Error getting documents: ", e)
            }
    }

    private fun getPeriodKey(workDate: Date, startDate: Date, endDate: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = workDate

        val startCalendar = Calendar.getInstance()
        startCalendar.time = startDate

        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        return when {
            startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) -> {
                "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}"  // Day-Month format
            }
            else -> {
                "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"  // Month-Year format
            }
        }
    }

    private fun updatePieChart(hoursWorked: Map<String, Float>) {
        val entries = ArrayList<PieEntry>()
        for ((period, hours) in hoursWorked) {
            entries.add(PieEntry(hours, period))
        }

        val dataSet = PieDataSet(entries, "Hours Worked")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()  // Customize colors
        val data = PieData(dataSet)

        binding.pieChart.data = data
        binding.pieChart.invalidate()  // Refresh the chart
    }

    private fun showDateRangePicker(userId: String) {
        val calendar = Calendar.getInstance()
        val startDatePicker = DatePickerDialog(this, { _, startYear, startMonth, startDayOfMonth ->
            val endDatePicker = DatePickerDialog(this, { _, endYear, endMonth, endDayOfMonth ->
                // Set the start and end date to be used in the query
                val startDate = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDayOfMonth)
                }.time

                val endDate = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDayOfMonth)
                }.time

                // Query the data based on custom date range
                queryTimesheetData(userId, startDate, endDate)

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            endDatePicker.show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        startDatePicker.show()
    }
}
