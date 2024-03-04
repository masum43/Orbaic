package com.orbaic.miner.homeNew

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale

class GetServerTime(private val latitude: Double, private val longitude: Double) {

    interface TimeApiService {
        @GET("Time/current/coordinate")
        suspend fun getCurrentTime(
            @Query("latitude") latitude: Double,
            @Query("longitude") longitude: Double
        ): Response<TimeResponse>
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
        val response = service.getCurrentTime(latitude, longitude)

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
