package com.example.mrtimepart2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mrtimepart2.ui.theme.CategoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Category : AppCompatActivity() {

    private lateinit var categoryTextInput: TextInputEditText
    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryList: MutableList<String>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var submitButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        sharedPreferences = getSharedPreferences("TimesheetPrefs", Context.MODE_PRIVATE)
        val fabBack = findViewById<FloatingActionButton>(R.id.fabBack)
        // Initialize views
        categoryTextInput = findViewById(R.id.category_text_input)
        addButton = findViewById(R.id.add_category_btn)
        recyclerView = findViewById(R.id.recyclerView)
        submitButton = findViewById(R.id.categorySubmitBtn)

        // Initialize RecyclerView
        categoryList = mutableListOf()
        categoryAdapter = CategoryAdapter(categoryList,sharedPreferences)
        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initially hide the input field AND the submit button
        categoryTextInput.visibility = View.GONE
        submitButton.visibility = View.GONE


        retrieveCategory()
        fabBack.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        addButton.setOnClickListener {
            Log.d("clicked", "FAB pressed")

            // Show the input field AND the submit button
            categoryTextInput.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE

            // Hide the Edit and Delete buttons in the RecyclerView
            categoryAdapter.showEditDeleteButtons = false
            categoryAdapter.notifyDataSetChanged()

            categoryTextInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(categoryTextInput, InputMethodManager.SHOW_IMPLICIT)
        }

        // Listener for the Submit button
        submitButton.setOnClickListener {
            val categoryName = categoryTextInput.text.toString()
            categoryList.add(categoryName)
            saveCategory()
            categoryAdapter.notifyItemInserted(categoryList.size - 1)
            categoryTextInput.text?.clear()

            // Hide the input field AND the submit button
            categoryTextInput.visibility = View.GONE
            submitButton.visibility = View.GONE

            // Show the Edit and Delete buttons again
            categoryAdapter.showEditDeleteButtons = true
            categoryAdapter.notifyDataSetChanged()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(categoryTextInput.windowToken, 0)
        }

        // Listener for the "Done" action on the keyboard
        categoryTextInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val categoryName = categoryTextInput.text.toString()
                categoryList.add(categoryName)
                categoryAdapter.notifyItemInserted(categoryList.size - 1)
                categoryTextInput.text?.clear()

                // Hide the input field and hide the keyboard
                categoryTextInput.visibility = View.GONE
                submitButton.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(categoryTextInput.windowToken, 0)

                // Show the Edit and Delete buttons again
                categoryAdapter.showEditDeleteButtons = true
                categoryAdapter.notifyDataSetChanged()

                true
            } else {
                false
            }
        }
    }
    // saves to gson
    private fun saveCategory() {
        val jsonString = gson.toJson(categoryList) // Serialize categoryList
        with(sharedPreferences.edit()) {
            putString("CATEGORY_LIST", jsonString) // Save to SharedPreferences
            apply()
        }
    }

    // Retrieves the category list from SharedPreferences
    private fun retrieveCategory() {
        val jsonString = sharedPreferences.getString("CATEGORY_LIST", null)
        if (!jsonString.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            val savedCategoryList: MutableList<String> = gson.fromJson(jsonString, type)
            categoryList.addAll(savedCategoryList)
            categoryAdapter.notifyDataSetChanged() // Update the adapter
        }
    }
}

