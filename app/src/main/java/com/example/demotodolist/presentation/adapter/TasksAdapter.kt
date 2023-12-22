package com.example.demotodolist.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.demotodolist.R
import com.example.demotodolist.base.Utils.Util.DateToString
import com.example.demotodolist.data.model.TaskCategoryInfo
import com.example.demotodolist.data.model.TaskInfo
import com.example.demotodolist.databinding.ItemTaskBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TasksAdapter : RecyclerView.Adapter<TasksAdapter.MyViewHolder>() {
    //region variable
    private val callback = object : DiffUtil.ItemCallback<TaskCategoryInfo>() {
        override fun areItemsTheSame(
            oldItem: TaskCategoryInfo,
            newItem: TaskCategoryInfo
        ): Boolean {
            return oldItem.taskInfo.id == newItem.taskInfo.id
        }

        override fun areContentsTheSame(
            oldItem: TaskCategoryInfo,
            newItem: TaskCategoryInfo
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, callback)
    //endregion

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.MyViewHolder {
        return MyViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TasksAdapter.MyViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class MyViewHolder(private val itemTaskBinding: ItemTaskBinding) :
        RecyclerView.ViewHolder(itemTaskBinding.root) {
        fun bind(taskCategoryInfo: TaskCategoryInfo) {
            itemTaskBinding.taskCategoryInfo = taskCategoryInfo
            //tim hieu
            itemTaskBinding.executePendingBindings()
            itemTaskBinding.isCompleted.isChecked=taskCategoryInfo.taskInfo.status
            val colorInt = Color.parseColor(taskCategoryInfo.categoryInfo[0].color)
            itemTaskBinding.isCompleted.buttonTintList = ColorStateList.valueOf(colorInt)
            itemTaskBinding.desciption.text=taskCategoryInfo.taskInfo.description
            itemTaskBinding.dueDate.text= DateToString.convertDateToString(taskCategoryInfo.taskInfo.date)

            itemTaskBinding.priority.buttonTintList=ColorStateList.valueOf(colorInt)
            when(taskCategoryInfo.taskInfo.priority){
                0->{
                    itemTaskBinding.priority.text="Low"
                }
                1->{
                    itemTaskBinding.priority.text="Medium"
                }
                2->{
                    itemTaskBinding.priority.text="High"
                }
            }


            itemTaskBinding.isCompleted.setOnCheckedChangeListener { _, it ->
                taskCategoryInfo.taskInfo.status = it
                onTaskStatusChangedListener?.let {
                    it(taskCategoryInfo.taskInfo)
                }
            }

            itemTaskBinding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(taskCategoryInfo)
                }
            }
        }
    }

    private var onItemClickListener: ((TaskCategoryInfo) -> Unit)? = null
    fun setOnItemClickListener(listener: (TaskCategoryInfo) -> Unit) {
        onItemClickListener = listener
    }

    private var onTaskStatusChangedListener: ((TaskInfo) -> Unit)? = null
    fun setOnTaskStatusChangedListener(listener: (TaskInfo) -> Unit) {
        onTaskStatusChangedListener = listener
    }

}