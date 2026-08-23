package com.example.taskmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ItemTaskBinding
import com.example.taskmanager.model.TaskResponse




class TaskAdapter(
    private var taskList: List<TaskResponse>,
    private val onCompleteClick: (TaskResponse) -> Unit,
    private val onDeleteClick: (TaskResponse) -> Unit,
    private val onItemClick : (TaskResponse) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.tvTaskTitle.text = task.title
        holder.binding.tvTaskDescription.text = task.description
        holder.binding.tvPriority.text = "Priority: ${task.priority}"
        holder.binding.tvCategory.text = "Category: ${task.category}"
        holder.binding.tvDeadline.text = "Deadline: ${task.deadline}"

        if (task.status == "COMPLETED") {
            holder.binding.btnComplete.text = "Completed"
            holder.binding.btnComplete.isEnabled = false
        } else {
            holder.binding.btnComplete.text = "Complete"
            holder.binding.btnComplete.isEnabled = true
        }

        holder.binding.btnEdit.setOnClickListener {
            onItemClick(task)
        }

        holder.binding.btnComplete.setOnClickListener {
            onCompleteClick(task)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(task)
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun updateList(newList: List<TaskResponse>) {
        taskList = newList
        notifyDataSetChanged()
    }
}