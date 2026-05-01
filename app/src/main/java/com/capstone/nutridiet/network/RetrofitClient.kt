package com.capstone.nutridiet.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ✅ your deployed Firebase URLs
    private const val ASK_URL =
        "https://ask-i3w4puyyoq-uc.a.run.app"

    private const val DIET_PLAN_URL =
        "https://generatedietplan-i3w4puyyoq-uc.a.run.app"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val askApi: RAGApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ASK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RAGApiService::class.java)
    }

    val dietPlanApi: RAGApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DIET_PLAN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RAGApiService::class.java)
    }
}