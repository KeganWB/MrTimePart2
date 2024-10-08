import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Locale

data class TimeSheetData(
    val name: String,
    val startTime: String,
    val endTime: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val category: String,
    val image: ByteArray? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createByteArray()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeByteArray(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TimeSheetData> {
        override fun createFromParcel(parcel: Parcel): TimeSheetData = TimeSheetData(parcel)
        override fun newArray(size: Int): Array<TimeSheetData?> = arrayOfNulls(size)
    }

    fun calculateHours(): Double {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val start = dateFormat.parse(startTime) ?: return 0.0
            val end = dateFormat.parse(endTime) ?: return 0.0

            var diffInMillis = end.time - start.time
            //This checks for if its the next day to add 24 hrs
            if (diffInMillis < 0) {
                diffInMillis += 24 * 60 * 60 * 1000
            }

            diffInMillis.toDouble() / (1000 * 60 * 60)
        } catch (e: Exception) {
            0.0
        }
    }

}
