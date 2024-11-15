import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Locale

data class TimeSheetData(
    val name: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val category: String = "",
    private val imageBase64: String? = null // Store image as Base64 string
) : Parcelable {

    // Convert Base64 to ByteArray
    val image: ByteArray?
        get() = imageBase64?.let { base64 ->
            try {
                Base64.decode(base64, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                null // Return null if the Base64 string is invalid
            }
        }

    // Constructor for ByteArray image
    constructor(
        name: String,
        startTime: String,
        endTime: String,
        startDate: String,
        endDate: String,
        description: String,
        category: String,
        image: ByteArray?
    ) : this(
        name,
        startTime,
        endTime,
        startDate,
        endDate,
        description,
        category,
        image?.let { Base64.encodeToString(it, Base64.DEFAULT) } // Convert to Base64 string
    )

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(startDate)
        parcel.writeString(endDate)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeString(imageBase64) // Write Base64 string
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TimeSheetData> {
        override fun createFromParcel(parcel: Parcel): TimeSheetData = TimeSheetData(parcel)
        override fun newArray(size: Int): Array<TimeSheetData?> = arrayOfNulls(size)
    }

    // Calculate hours method
    fun calculateHours(): Double {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            val start = dateFormat.parse(startTime) ?: return 0.0
            val end = dateFormat.parse(endTime) ?: return 0.0

            var diffInMillis = end.time - start.time
            if (diffInMillis < 0) {
                diffInMillis += 24 * 60 * 60 * 1000
            }

            diffInMillis.toDouble() / (1000 * 60 * 60)
        } catch (e: Exception) {
            0.0
        }
    }
}
