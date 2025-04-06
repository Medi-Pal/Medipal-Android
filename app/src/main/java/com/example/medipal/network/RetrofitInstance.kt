package com.example.medipal.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Singleton for Retrofit instance to handle API requests
 */
object RetrofitInstance {
    private const val TAG = "RetrofitInstance"
    
    // Use URL without trailing slash to ensure path segments are correctly joined
    // Using the Vercel URL since that's what's in the QR code
    private const val BASE_URL = "https://medipal-web-ymqf.vercel.app"
    // private const val BACKUP_URL = "https://medipal-backend.onrender.com"
    private const val TIMEOUT = 120L // in seconds - increased timeout for slower server responses

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .addInterceptor { chain ->
                val request = chain.request()
                Log.d(TAG, "Request URL: ${request.url}")
                Log.d(TAG, "Request headers: ${request.headers}")
                
                try {
                    val response = chain.proceed(request)
                    Log.d(TAG, "Response code: ${response.code}")
                    if (!response.isSuccessful) {
                        Log.e(TAG, "API Error: ${response.code} - ${response.message}")
                    }
                    response
                } catch (e: Exception) {
                    Log.e(TAG, "Network error: ${e.message}", e)
                    throw e
                }
            }
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            
        // Enable this to bypass certificate validation for testing (caution: security risk)
        // builder.hostnameVerifier { _, _ -> true }
            
        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        try {
            Log.d(TAG, "Initializing Retrofit with base URL: $BASE_URL/")
            Retrofit.Builder()
                .baseUrl("$BASE_URL/") // Explicitly add trailing slash here
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Retrofit", e)
            throw e
        }
    }

    // Create API service instances
    val apiService: ApiService by lazy {
        try {
            Log.d(TAG, "Creating API service")
            retrofit.create(ApiService::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating API service", e)
            throw e
        }
    }
} 