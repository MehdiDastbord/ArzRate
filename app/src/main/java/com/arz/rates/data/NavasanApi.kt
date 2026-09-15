package com.arz.rates.data

import com.arz.rates.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface NavasanApi {
    @GET("latest/")
    suspend fun latest(@Query("api_key") apiKey: String): Map<String, Rate>
}

class RatesRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.navasan.tech/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api = retrofit.create(NavasanApi::class.java)

    suspend fun fetch(): List<RateItem> = api.latest(BuildConfig.NAVASAN_API_KEY)
        .map { RateItem(it.key, it.value) }
        .sortedBy { it.title.lowercase() }
}
