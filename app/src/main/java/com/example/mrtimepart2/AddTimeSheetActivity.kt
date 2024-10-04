package com.example.mrtimepart2

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.io.ByteArrayOutputStream

class AddTimeSheetActivity : DialogFragment() {

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
    }
    private lateinit var imagePreview: ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = inflater.inflate(R.layout.activity_timesheetdetails, null)

        // Find your views
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextStartTime = dialogView.findViewById<EditText>(R.id.editTextStartTime)
        val editTextEndTime = dialogView.findViewById<EditText>(R.id.editTextEndTime)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val buttonAddPhoto = dialogView.findViewById<Button>(R.id.buttonAddPhoto)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.buttonSubmit)
        imagePreview = dialogView.findViewById(R.id.imagePrev)

        //Add Spinner Default Categories
        val categories = listOf("Other")
        // Create an ArrayAdapter using the default spinner layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinnerCategory.adapter = adapter

        // Set up button to take or select a photo
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
                    "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        // Add custom listener for submit button
        buttonSubmit.setOnClickListener {
            // Collect the entered information
            val name = editTextName.text.toString()
            val startTime = editTextStartTime.text.toString()
            val endTime = editTextEndTime.text.toString()
            val description = editTextDescription.text.toString()
            val category = spinnerCategory.selectedItem.toString()

            // Convert the image to ByteArray (if available)
            val imageByteArray = (imagePreview.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }

            // Create a TimeSheetData object
            val timeSheetData = TimeSheetData(
                name = name,
                startTime = startTime,
                endTime = endTime,
                description = description,
                category = category,
                image = imageByteArray // Add the image ByteArray if available
            )

            // Pass TimeSheetData to the target activity
            val intent = Intent(requireContext(), TimeSheetActivity::class.java).apply {
                putExtra("timeSheetData", timeSheetData) // Put Parcelable object
            }
            startActivity(intent)

            // Dismiss the dialog after submitting
            dismiss()
        }

        // Remove the default positive button and keep only the Cancel button
        builder.setView(dialogView)
            .setTitle("Add New Timesheet")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // This will close the dialog without doing anything
            }

        return builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        // Display the captured image in the ImageView
                        imagePreview.setImageBitmap(it)
                        imagePreview.visibility = ImageView.VISIBLE
                        Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        // Load and display the selected image in the ImageView
                        imagePreview.setImageURI(it)
                        imagePreview.visibility = ImageView.VISIBLE
                        Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
