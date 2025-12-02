package com.example.agrochain.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Production backend URL - replace with your deployed API domain (must include trailing `/api/`)
    private const val PROD_BASE_URL = "https://api.example.com/api/"
    // Local development URL for physical device testing. Use adb reverse (see instructions) or
    // replace with your machine LAN IP if testing over Wi-Fi.
    private const val DEV_BASE_URL = "http://127.0.0.1:3000/api/"

    // Use debug base URL when running a debug build on device/emulator
    private val BASE_URL: String = try {
        val debug = com.example.agrochain.BuildConfig.DEBUG
        if (debug) DEV_BASE_URL else PROD_BASE_URL
    } catch (e: Exception) {
        PROD_BASE_URL
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // Method to update base URL dynamically (useful for production)
    fun updateBaseUrl(baseUrl: String): ApiService {
        val newRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return newRetrofit.create(ApiService::class.java)
    }
}


