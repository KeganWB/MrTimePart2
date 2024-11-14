package com.example.mrtimepart2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mrtimepart2.ui.theme.CategoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Category : AppCompatActivity() {

    private lateinit var categoryTextInput: TextInputEditText
    private lateinit var addButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryList: MutableList<String>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var submitButton: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val fabBack = findViewById<FloatingActionButton>(R.id.fabBack)
        categoryTextInput = findViewById(R.id.category_text_input)
        addButton = findViewById(R.id.add_category_btn)
        recyclerView = findViewById(R.id.recyclerView)
        submitButton = findViewById(R.id.categorySubmitBtn)

        categoryList = mutableListOf()
        retrieveCategory()

        categoryAdapter = CategoryAdapter(categoryList) { updatedList -> saveCategory(updatedList) }
        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        categoryTextInput.visibility = View.GONE
        submitButton.visibility = View.GONE

        fabBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        addButton.setOnClickListener {
            categoryTextInput.visibility = View.VISIBLE
            submitButton.visibility = View.VISIBLE

            categoryAdapter.showEditDeleteButtons = false
            categoryAdapter.notifyDataSetChanged()

            categoryTextInput.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(categoryTextInput, InputMethodManager.SHOW_IMPLICIT)
        }

        submitButton.setOnClickListener {
            val categoryName = categoryTextInput.text.toString()
            if (categoryName.isNotBlank()) {
                categoryList.add(categoryName)
                saveCategory(categoryList) //Save updated category list to Firestore
                categoryAdapter.notifyItemInserted(categoryList.size - 1)
                categoryTextInput.text?.clear()

                //Hide input field and submit button
                categoryTextInput.visibility = View.GONE
                submitButton.visibility = View.GONE

                //Show Edit and Delete buttons again
                categoryAdapter.showEditDeleteButtons = true
                categoryAdapter.notifyDataSetChanged()

                //Hide keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(categoryTextInput.windowToken, 0)
            }
        }
    }

    //Saving categories to Firestore
    private fun saveCategory(categoryList: MutableList<String>) {
        userId?.let { uid ->
            val data = hashMapOf("categories" to categoryList)
            firestore.collection("users").document(uid)
                .set(data)
                .addOnSuccessListener {
                    Log.d("Firestore", "Categories successfully saved!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error saving categories", e)
                }
        }
    }

    //Retrieving categories from Firestore
    private fun retrieveCategory() {
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("categories")) {
                        val savedCategoryList = document.get("categories") as? List<String>
                        if (savedCategoryList != null) {
                            categoryList.clear()
                            categoryList.addAll(savedCategoryList)
                            categoryAdapter.notifyDataSetChanged()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error retrieving categories", e)
                }
        }
    }
}
