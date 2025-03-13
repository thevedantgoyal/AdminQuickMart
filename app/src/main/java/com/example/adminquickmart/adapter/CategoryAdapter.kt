package com.example.adminquickmart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminquickmart.databinding.ItemViewProductCategoryBinding
import com.example.adminquickmart.models.Category

class CategoryAdapter(val categoryArray: ArrayList<Category>, val onCtegoryClicked: (Category) -> Unit): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(val binding : ItemViewProductCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.CategoryViewHolder {
        return CategoryViewHolder(ItemViewProductCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return categoryArray.size
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryViewHolder, position: Int) {
        val category = categoryArray[position]
        holder.binding.apply {
            ivCategoryImage.setImageResource(category.image)
            tvCategoryText.text = category.categoryTitle
        }
        holder.itemView.setOnClickListener {
            onCtegoryClicked(category)
        }

    }



}