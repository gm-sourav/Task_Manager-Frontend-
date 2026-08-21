package com.example.taskmanager.model

data class RegisterRequest(
    val name : String,
    val email : String,
    val password : String
)

data class LoginRequest(
    val  email : String,
    val password: String
)

data class AuthResponse(
    val token : String,
    val message : String
)
