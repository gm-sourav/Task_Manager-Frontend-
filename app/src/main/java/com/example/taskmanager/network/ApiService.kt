package com.example.taskmanager.network

import com.example.taskmanager.model.AuthResponse
import com.example.taskmanager.model.LoginRequest
import com.example.taskmanager.model.RegisterRequest
import com.example.taskmanager.model.TaskRequest
import com.example.taskmanager.model.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/tasks")
    suspend fun getAllTask(
        @Header("authorization") token: String
        ): Response<List<TaskResponse>>

    @GET("api/tasks")
    suspend fun getTaskByCategory(
        @Header("authorization") token: String,
        @Query("category") category: String
        ): Response<List<TaskResponse>>

    @POST("api/tasks")
    suspend fun createTask(
        @Header("authorization") token: String,
        @Body request: TaskRequest
    ): Response<TaskResponse>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Header("authorization") token: String,
        @Path("id") taskId: Long,
        @Body request: TaskRequest
    ): Response<TaskResponse>


    @HTTP(method = "api/tasks/{id}/status")
    suspend fun updateStatus(
        @Header("authorization") token: String,
        @Path("id") taskId: Long,
        @Query("status") status: String
    ): Response<TaskResponse>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("id") taskId: Long
    ): Response<Unit>

}