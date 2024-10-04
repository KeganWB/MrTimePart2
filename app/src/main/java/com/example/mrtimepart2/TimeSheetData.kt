package com.example.mrtimepart2

data class TimeSheetData(
    val name: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val category: String,
     // Optional for the photo
)