package com.example.taskmanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.taskmanager.databinding.ActivityLoginBinding
import com.example.taskmanager.model.LoginRequest
import com.example.taskmanager.network.RetrofitClient
import com.example.taskmanager.utils.TokenManager
import kotlinx.coroutines.launch
import kotlin.text.toInt


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(24.dpToPx(), systemBars.top + 24.dpToPx(), 24.dpToPx(), systemBars.bottom + 24.dpToPx())
            insets
        }



        tokenManager = TokenManager(this)

        if (tokenManager.isLoggedIn()) {
            goToTaskList()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))

                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    tokenManager.saveToken(token)
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    goToTaskList()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun goToTaskList() {

        startActivity(Intent(this, TaskListActivity::class.java))
        finish()
    }




    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}

