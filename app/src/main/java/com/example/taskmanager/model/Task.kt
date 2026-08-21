package com.example.taskmanager.model

data class TaskRequest(
    val title : String,
    val description : String,
    val deadline : String,
    val priority : String,
    val category: String
)

data class TaskResponse(
    val id : Long,
    val title : String,
    val description : String,
    val deadline : String,
    val priority : String,
    val category: String,
    val status : String,
    val createdAt : String
)

