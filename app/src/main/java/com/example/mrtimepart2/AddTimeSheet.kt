package com.example.mrtimepart2
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class AddTimeSheet : DialogFragment() {

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 101
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_new_timesheet, null)

        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextStartTime = dialogView.findViewById<EditText>(R.id.editTextStartTime)
        val editTextEndTime = dialogView.findViewById<EditText>(R.id.editTextEndTime)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextCategory)
        val buttonAddPhoto = dialogView.findViewById<Button>(R.id.buttonAddPhoto)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.buttonSubmit)

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

        // Set up submit button to handle adding the new timesheet
        buttonSubmit.setOnClickListener {
            val name = editTextName.text.toString()
            val startTime = editTextStartTime.text.toString()
            val endTime = editTextEndTime.text.toString()
            val description = editTextDescription.text.toString()
            val category = editTextCategory.text.toString()

            // Validate input and add the timesheet (pass back to activity or save directly)


            dismiss() // Close the dialog after submitting
        }

        return AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setTitle("Add New Timesheet")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1) { // -1 corresponds to Activity.RESULT_OK
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        // Handle the image from the camera, e.g., display in an ImageView
                        Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        // Handle the image from the gallery, e.g., display in an ImageView
                        Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}