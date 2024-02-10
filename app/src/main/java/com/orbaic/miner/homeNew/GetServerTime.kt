package com.orbaic.miner.homeNew

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class GetServerTime {

    interface TimeApiService {
        @GET("Time/current/coordinate?latitude=23.777176&longitude=90.399452")
        suspend fun getCurrentTime(): Response<TimeResponse>
    }

    data class TimeResponse(
        @SerializedName("dateTime")
        val dateTime: String
    )

    suspend fun getTime(): Long {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://timeapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(TimeApiService::class.java)
        val response = service.getCurrentTime()

        if (response.isSuccessful) {
            val timeResponse = response.body()
            val truncatedString = timeResponse?.dateTime?.substring(0, 26)

            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.ENGLISH)
            val date = formatter.parse(truncatedString)
            return date?.time ?: 0
        } else {
            throw Exception("Error fetching current time: ${response.message()}")
        }
    }
}
