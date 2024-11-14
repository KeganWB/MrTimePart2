package com.example.mrtimepart2

import TimeSheetData
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTimeSheetActivity : DialogFragment() {

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
    }

    private lateinit var imagePreview: ImageView
    var db = Firebase.firestore

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = inflater.inflate(R.layout.activity_timesheetdetails, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextStartTime = dialogView.findViewById<EditText>(R.id.editTextStartTime)
        val editTextEndTime = dialogView.findViewById<EditText>(R.id.editTextEndTime)
        val editTextStartDate = dialogView.findViewById<EditText>(R.id.editTextStartDate) // New
        val editTextEndDate = dialogView.findViewById<EditText>(R.id.editTextEndDate) // New
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val buttonAddPhoto = dialogView.findViewById<Button>(R.id.buttonAddPhoto)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.buttonSubmit)
        imagePreview = dialogView.findViewById(R.id.imagePrev)


        val categories = retrieveCategories()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter


        editTextStartDate.setOnClickListener {
            showDatePickerDialog(editTextStartDate)
        }

        editTextEndDate.setOnClickListener {
            showDatePickerDialog(editTextEndDate)
        }


        editTextStartTime.setOnClickListener {
            showTimePickerDialog(editTextStartTime)
        }

        editTextEndTime.setOnClickListener {
            showTimePickerDialog(editTextEndTime)
        }


        buttonAddPhoto.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Option")
            builder.setItems(options) { dialog, which ->
                when (options[which]) {
                    "Take Photo" -> {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
                        } else {
                            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "Choose from Gallery" -> {
                        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhotoIntent, REQUEST_GALLERY)
                    }
                    "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }

        buttonSubmit.setOnClickListener {
            val name = editTextName.text.toString()
            val startTime = editTextStartTime.text.toString()
            val endTime = editTextEndTime.text.toString()
            val startDate = editTextStartDate.text.toString()
            val endDate = editTextEndDate.text.toString()
            val description = editTextDescription.text.toString()
            val category = spinnerCategory.selectedItem.toString()

            // Convert the image to a byte array
            val imageByteArray = (imagePreview.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }

            // Create a new TimeSheetData instance
            val timeSheetData = TimeSheetData(
                name = name,
                startTime = startTime,
                endTime = endTime,
                startDate = startDate,
                endDate = endDate,
                description = description,
                category = category,
                image = imageByteArray // Pass the byte array, will be converted to Base64 internally
            )
            db.collection("User")
                .add(timeSheetData)
            // Notify listener about the new timesheet data
            listener?.onTimesheetAdded(timeSheetData)
            dismiss()
        }


        builder.setView(dialogView)
            .setTitle("Add New Timesheet")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
            editText.setText(dateFormat.format(selectedDate.time))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            editText.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun retrieveCategories(): List<String> {
        val sharedPreferences = requireContext().getSharedPreferences("TimesheetPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonString = sharedPreferences.getString("CATEGORY_LIST", null)
        return if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            listOf("Other") //this is default Category
        }
    }

    interface OnTimesheetAddedListener {
        fun onTimesheetAdded(timeSheetData: TimeSheetData)
    }

    private var listener: OnTimesheetAddedListener? = null

    fun setOnTimesheetAddedListener(listener: OnTimesheetAddedListener) {
        this.listener = listener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        imagePreview.setImageBitmap(it)
                        imagePreview.visibility = ImageView.VISIBLE
                        Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        imagePreview.setImageURI(it)
                        imagePreview.visibility = ImageView.VISIBLE
                        Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}
