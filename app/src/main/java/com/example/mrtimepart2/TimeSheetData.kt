package com.example.mrtimepart2

import android.os.Parcel
import android.os.Parcelable

data class TimeSheetData(
    val name: String,
    val startTime: String,
    val endTime: String,
    val startDate: String,     // New field
    val endDate: String,       // New field
    val description: String,
    val category: String,
    val image: ByteArray? = null // Optional for the image
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "", // Reading startDate
        parcel.readString() ?: "", // Reading endDate
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createByteArray() // Read image as ByteArray
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(startDate)  // Writing startDate
        parcel.writeString(endDate)    // Writing endDate
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeByteArray(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TimeSheetData> {
        override fun createFromParcel(parcel: Parcel): TimeSheetData = TimeSheetData(parcel)
        override fun newArray(size: Int): Array<TimeSheetData?> = arrayOfNulls(size)
    }
}
