package com.theappmakerbuddy.recipeapp.data.remote.custom

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val invocation = request.tag(Invocation::class.java)
        val apiKeyAnnotation = invocation?.method()?.getAnnotation(ApiKey::class.java)
        
        return if (apiKeyAnnotation != null) {
            val apiKey = apiKeyAnnotation.value
            val newRequest = request.newBuilder()
                .addHeader("x-api-key", apiKey)
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }
    }
}
