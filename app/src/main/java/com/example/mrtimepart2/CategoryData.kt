package com.example.mrtimepart2

import android.os.Parcel
import android.os.Parcelable

data class CategoryData(
    val categoryName: String = ""
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryName)

    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CategoryData> {
        override fun createFromParcel(parcel: Parcel): CategoryData = CategoryData(parcel)
        override fun newArray(size: Int): Array<CategoryData?> = arrayOfNulls(size)
    }
}

