package com.example.demotodolist.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.demotodolist.R
import com.example.demotodolist.data.model.NoOfTaskForEachCategory
import com.example.demotodolist.databinding.ItemCategoriesBinding

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    //region variable
    private val callback = object : DiffUtil.ItemCallback<NoOfTaskForEachCategory>() {
        override fun areItemsTheSame(
            oldItem: NoOfTaskForEachCategory,
            newItem: NoOfTaskForEachCategory
        ): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(
            oldItem: NoOfTaskForEachCategory,
            newItem: NoOfTaskForEachCategory
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, callback)

    //endregion
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_categories,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val itemCategoriesBinding: ItemCategoriesBinding) :
        RecyclerView.ViewHolder(itemCategoriesBinding.root) {
        fun bind(noOfTaskForEachCategory: NoOfTaskForEachCategory) {
            itemCategoriesBinding.noOfTaskOfCategory = noOfTaskForEachCategory
            itemCategoriesBinding.executePendingBindings()
            itemCategoriesBinding.categoryCount.text = noOfTaskForEachCategory.count.toString()
            itemCategoriesBinding.categoryName.text = noOfTaskForEachCategory.category
            val categoryColorTop = Color.parseColor(noOfTaskForEachCategory.color)
            itemCategoriesBinding.categoryColorTop.setBackgroundColor(categoryColorTop)
            itemCategoriesBinding.categoryColor.setBackgroundColor(categoryColorTop)
            itemCategoriesBinding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(noOfTaskForEachCategory.category)
                }
            }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }
}