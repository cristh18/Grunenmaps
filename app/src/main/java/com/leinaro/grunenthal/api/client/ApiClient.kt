package com.leinaro.grunenthal.api.client

import android.content.Context
import com.leinaro.grunenthal.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val REQUEST_TIMEOUT = 60L

fun getRemoteClient(context: Context): Retrofit {
    return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(context.getString(R.string.base_url))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun getLocalClient(context: Context): Retrofit {
    return Retrofit.Builder()
            .client(getOkHttpClient())
            .baseUrl(context.getString(R.string.local_url))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}


fun getOkHttpClient(): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)

    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    httpClient.addInterceptor(interceptor)

    httpClient.addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
        requestBuilder.method(chain.request().method(), chain.request().body())

        val request = requestBuilder.build()
        chain.proceed(request)
    }
    return httpClient.build()
}