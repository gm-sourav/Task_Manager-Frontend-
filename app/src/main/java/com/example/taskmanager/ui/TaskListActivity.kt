package com.example.taskmanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.databinding.ActivityTaskListBinding
import com.example.taskmanager.model.TaskResponse
import com.example.taskmanager.network.RetrofitClient
import com.example.taskmanager.utils.TokenManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class TaskListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        setupRecyclerView()

        binding.btnLogout.setOnClickListener {
            tokenManager.clearToken()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            taskList = emptyList(),
            onCompleteClick = { task -> completeTask(task) },
            onDeleteClick = { task -> deleteTask(task) }
        )
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = adapter
    }

    private fun loadTasks() {
        val token = "Bearer " + tokenManager.getToken()
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAllTask(token)
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val tasks = response.body()!!
                    adapter.updateList(tasks)

                    if (tasks.isEmpty()) {
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                        binding.recyclerViewTasks.visibility = View.GONE
                    } else {
                        binding.tvEmptyMessage.visibility = View.GONE
                        binding.recyclerViewTasks.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@TaskListActivity, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@TaskListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun completeTask(task: TaskResponse) {
        val token = "Bearer " + tokenManager.getToken()
        val emptyBody = "".toRequestBody("text/plain".toMediaTypeOrNull())

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.updateStatus(token, task.id, "COMPLETED", emptyBody)
                if (response.isSuccessful) {
                    Toast.makeText(this@TaskListActivity, "Task completed!", Toast.LENGTH_SHORT).show()
                    loadTasks()
                } else {
                    Toast.makeText(this@TaskListActivity, "Failed to update task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TaskListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTask(task: TaskResponse) {
        val token = "Bearer " + tokenManager.getToken()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.deleteTask(token, task.id)
                if (response.isSuccessful) {
                    Toast.makeText(this@TaskListActivity, "Task deleted!", Toast.LENGTH_SHORT).show()
                    loadTasks()
                } else {
                    Toast.makeText(this@TaskListActivity, "Failed to delete task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TaskListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}