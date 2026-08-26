package com.example.taskmanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.taskmanager.databinding.ActivityRegisterBinding
import com.example.taskmanager.model.RegisterRequest
import com.example.taskmanager.network.RetrofitClient
import com.example.taskmanager.utils.TokenManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(24.dpToPx(), systemBars.top + 24.dpToPx(), 24.dpToPx(), systemBars.bottom + 24.dpToPx())
            insets
        }


        tokenManager = TokenManager(this)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }

    }

    private fun registerUser(name: String, email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.register(
                    RegisterRequest(name, email, password)
                )

                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    tokenManager.saveToken(token)
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, TaskListActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed. Email might already exist.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}