package com.example.mrtimepart2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mrtimepart2.ui.theme.CategoryAdapter // Assuming this is your adapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class Category : AppCompatActivity() {

    private lateinit var categoryTextInput: TextInputEditText
    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryList: MutableList<String>
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category) // Set the layout

        // Initialize views
        categoryTextInput = findViewById(R.id.category_text_input)
        addButton = findViewById(R.id.add_category_btn) // Use the correct ID
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize RecyclerView
        categoryList = mutableListOf()
        categoryAdapter = CategoryAdapter(categoryList)
        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addButton.setOnClickListener {
            Log.d("clicked","FAB pressed")
            categoryTextInput.visibility = View.VISIBLE
            categoryTextInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(categoryTextInput, InputMethodManager.SHOW_IMPLICIT)
        }

        categoryTextInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val categoryName = categoryTextInput.text.toString()
                categoryList.add(categoryName)
                categoryAdapter.notifyItemInserted(categoryList.size - 1)
                categoryTextInput.text?.clear()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(categoryTextInput.windowToken, 0)
                true
            } else {
                false
            }
        }
    }
}