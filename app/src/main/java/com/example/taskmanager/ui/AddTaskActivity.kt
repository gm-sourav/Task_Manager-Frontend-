package com.example.taskmanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.taskmanager.databinding.ActivityAddTaskBinding
import com.example.taskmanager.model.TaskRequest
import com.example.taskmanager.network.RetrofitClient
import com.example.taskmanager.utils.TokenManager
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        binding.etDeadline.setOnClickListener {
            showDatePicker()
        }

        binding.btnSaveTask.setOnClickListener {
            saveTask()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedMonth = String.format("%02d", selectedMonth + 1)
            val formattedDay = String.format("%02d", selectedDay)
            binding.etDeadline.setText("$selectedYear-$formattedMonth-$formattedDay")
        }, year, month, day).show()
    }

    private fun saveTask() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val deadline = binding.etDeadline.text.toString().trim()

        if (title.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Title and Deadline are required", Toast.LENGTH_SHORT).show()
            return
        }

        val priority = when (binding.rgPriority.checkedRadioButtonId) {
            binding.rbHigh.id -> "HIGH"
            binding.rbMedium.id -> "MEDIUM"
            else -> "LOW"
        }

        val category = when (binding.rgCategory.checkedRadioButtonId) {
            binding.rbWork.id -> "WORK"
            binding.rbPersonal.id -> "PERSONAL"
            else -> "STUDY"
        }

        val token = "Bearer " + tokenManager.getToken()
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSaveTask.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.createTask(
                    token,
                    TaskRequest(title, description, deadline, priority, category)
                )

                binding.progressBar.visibility = View.GONE
                binding.btnSaveTask.isEnabled = true

                if (response.isSuccessful) {
                    Toast.makeText(this@AddTaskActivity, "Task added successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddTaskActivity, "Failed to add task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnSaveTask.isEnabled = true
                Toast.makeText(this@AddTaskActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}