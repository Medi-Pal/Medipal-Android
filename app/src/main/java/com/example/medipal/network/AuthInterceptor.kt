package com.example.medipal.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add authentication headers to all requests
 */
class AuthInterceptor : Interceptor {
    private val TAG = "AuthInterceptor"
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        Log.d(TAG, "Intercepting request: ${originalRequest.url}")
        
        // Get auth token here from a secure storage
        val token = getAuthToken()
        
        // Skip authentication for some endpoints if token is empty
        val path = originalRequest.url.encodedPath
        if (token.isEmpty() && !requiresAuth(path)) {
            Log.d(TAG, "Skipping auth header for public endpoint: $path")
            return chain.proceed(originalRequest)
        }
        
        // Create a new request with the auth header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
            
        Log.d(TAG, "Added auth header to request")
        return chain.proceed(newRequest)
    }
    
    private fun requiresAuth(path: String): Boolean {
        // Define which endpoints require authentication
        // For testing purposes, we're allowing all endpoints to work without auth
        // In production, this would be a list of protected endpoints
        return false
    }
    
    private fun getAuthToken(): String {
        // In a real app, retrieve the token from a secure storage
        return ""
    }
} 