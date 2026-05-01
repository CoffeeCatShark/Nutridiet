package com.capstone.nutridiet.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RAGApiService {

    @GET(".")
    suspend fun healthCheck(): HealthCheckResponse

    @POST(".")
    suspend fun ask(@Body request: QueryRequest): QueryResponse

    @POST(".")
    suspend fun generateDietPlan(@Body request: DietPlanRequest): DietPlanResponse
}