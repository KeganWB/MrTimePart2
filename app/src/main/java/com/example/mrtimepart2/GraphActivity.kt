/*

package com.example.mrtimepart2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mrtimepart2.databinding.ActivityGraphsBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pieChart = binding.pieChart

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(30f, "January"))
        entries.add(PieEntry(20f, "February"))
        entries.add(PieEntry(50f, "March"))

        val dataSet = PieDataSet(entries, "Monthly Sales")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // refresh the chart
    }
}

*/
