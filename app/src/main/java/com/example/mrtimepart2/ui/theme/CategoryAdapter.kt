package com.example.mrtimepart2.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mrtimepart2.R


class CategoryAdapter(private val categoryList: MutableList<String>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.category_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryTextView.text = categoryList[position]
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}