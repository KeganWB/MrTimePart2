package com.example.mrtimepart2.ui.theme

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mrtimepart2.R

class CategoryAdapter(private val categoryList: MutableList<String>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    var showEditDeleteButtons = true

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryTextView.text = categoryList[position]

        // Set the visibility of the Edit and Delete buttons
        holder.editButton.visibility = if (showEditDeleteButtons) View.VISIBLE else View.GONE
        holder.deleteButton.visibility = if (showEditDeleteButtons) View.VISIBLE else View.GONE

        holder.deleteButton.setOnClickListener {
            val dialog = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Delete") { dialog, _ ->
                    categoryList.removeAt(position)
                    notifyItemRemoved(position)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }

        holder.editButton.setOnClickListener {
            val editText = EditText(holder.itemView.context)
            editText.setText(categoryList[position])

            val dialog = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Edit Category")
                .setView(editText)
                .setPositiveButton("Save") { dialog, _ ->
                    val newName = editText.text.toString()
                    if (newName.isNotBlank()) {
                        categoryList[position] = newName
                        notifyItemChanged(position)
                    } else {
                        // Handle empty name (e.g., show a Toast message)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}