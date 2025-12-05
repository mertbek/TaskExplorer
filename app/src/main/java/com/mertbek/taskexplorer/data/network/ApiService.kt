package com.mertbek.taskexplorer.data.network

import com.mertbek.taskexplorer.data.model.LoginRequest
import com.mertbek.taskexplorer.data.model.LoginResponse
import com.mertbek.taskexplorer.data.model.TaskItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("dev/index.php/login")
    suspend fun login(
        @Header("Authorization") authHeader: String,
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("dev/index.php/v1/tasks/select")
    suspend fun getTasks(
        @Header("Authorization") token: String
    ): Response<List<TaskItem>>
}