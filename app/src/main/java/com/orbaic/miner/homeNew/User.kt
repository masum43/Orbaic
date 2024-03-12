package com.orbaic.miner.homeNew

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.orbaic.miner.MyApp
import com.orbaic.miner.common.Config
import com.orbaic.miner.common.Constants
import com.orbaic.miner.common.SpManager
import java.util.concurrent.TimeUnit
import kotlin.math.abs

data class User(
    var birthdate: String = "",
    var click: String = "",
    var country: String = "",
    var email: String = "",
    var extra1: String = "",
    var extra2: String = "",
    var extra3: String = "",
    var id: String = "",
    var miningStartTime: String = "",
    var mining_count: String = "",
    var qz_count: String = "",
    var name: String = "",
    var phone: String = "",
    var profile_image: String = "",
    var point: Any = "",
    var referralPoint: Any = "",
    var referral: String = "",
    var referralButton: String = "",
    var referredBy: String = "",
    var referredByCode: String = "",
    var type: String = ""
) {


    suspend fun isQuizWithin12Hours(): TimeStatus {
        if (extra1.isEmpty()) {
            return TimeStatus(0, "Quiz end time is empty.")
        }

//        val serverTime = getServerTime()
        val currentTime = System.currentTimeMillis()
//        val differenceBetweenServerAndCurrentTime = abs(currentTime - serverTime)

        // Check if the time difference exceeds 5 minutes
//        if (differenceBetweenServerAndCurrentTime > 5 * 60 * 1000) {
//            val hours = (differenceBetweenServerAndCurrentTime / (1000 * 60 * 60)) % 24
//            return TimeStatus(2, "Time difference between server and device is too large: $hours hours.")
//        }

        val quizEndTime = extra1.toLongOrNull() ?: return TimeStatus(0, "Invalid mining end time format.")
//        val timeDifference = quizEndTime - serverTime
        val timeDifference = quizEndTime - currentTime


        if (timeDifference <= 0) {
            return TimeStatus(0, "Quiz end time is not within 12 hours.")
        }

        val diffHours = timeDifference / (1000 * 60 * 60)

        Log.e("isQuizWithin12Hours", "quizEndTimeTimestamp: $quizEndTime")
//        Log.e("isQuizWithin12Hours", "serverTime: $serverTime")
        Log.e("isQuizWithin12Hours", "currentTime: $currentTime")
        Log.e("isQuizWithin12Hours", "timeDifference: $timeDifference")
        Log.e("isQuizWithin12Hours", "diffHours: $diffHours")

        return if (diffHours < 12) {
            TimeStatus(1)
        } else {
            TimeStatus(0, "Quiz end time is not within 12 hours.")
        }
    }

    suspend fun isMiningWithin24Hours(): TimeStatus {
        Log.e("isMiningWithin24Hours", "miningStartTime: $miningStartTime")
        if (miningStartTime.isEmpty()) {
            return TimeStatus(Constants.STATE_MINING_FINISHED, "Mining start time is empty.")
        }

        if (ContextCompat.checkSelfPermission(
                MyApp.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return TimeStatus(Constants.STATE_MINING_LOCATION_NOT_GRANTED, "Location permission not granted.")
        }

        val serverTime = getServerTime()
        val currentTime = System.currentTimeMillis()
        val diffBetweenCurrentAndServerTime = abs(currentTime - serverTime)
        Log.e("isMiningWithin24Hours", "serverTime: $serverTime")
        Log.e("isMiningWithin24Hours", "currentTime: $currentTime")
        Log.e("isMiningWithin24Hours", "diffBetweenCurrentAndServerTime: $diffBetweenCurrentAndServerTime")

        // Check if the time difference exceeds serverAllowedTimeDifference
        if (diffBetweenCurrentAndServerTime > Config.serverAllowedTimeDifference * 60 * 1000) {
            val days = diffBetweenCurrentAndServerTime / (1000 * 60 * 60 * 24)
            val hours = (diffBetweenCurrentAndServerTime / (1000 * 60 * 60)) % 24
            val minutes = (diffBetweenCurrentAndServerTime / (1000 * 60)) % 60
            val seconds = (diffBetweenCurrentAndServerTime / 1000) % 60
            return TimeStatus(Constants.STATE_MINING_DATE_DIFF_SERVER, "Time difference between server and device is too large: $days days, $hours hours, $minutes minutes, $seconds seconds.\n\nOr if you fix your time just now but still the error showing then click on Clear Data.")
        }

//        if (diffBetweenCurrentAndServerTime <= 0) {
//            return TimeStatus(Constants.STATE_MINING_ERROR, "Mining start time is not within 24 hours.")
//        }

        val miningStartTimeTimestamp = miningStartTime.toLongOrNull() ?: return TimeStatus(0, "Invalid mining start time format.")
        val diff = abs(serverTime - miningStartTimeTimestamp)
        val diffHours = diff / (1000 * 60 * 60)

        Log.e("isMiningWithin24Hours", "diffHours: $diffHours")
        Log.e("isMiningWithin24Hours", "extra3: $extra3")
        return if (diffHours < 24) {
            TimeStatus(Constants.STATE_MINING_ON_GOING, "Mining start time is within 24 hours.")
        } else {
            if (extra3.toInt() == 0) {
                TimeStatus(Constants.STATE_MINING_POINTS_NOT_GIVEN, "Mining finished but points not given")
            }
            else TimeStatus(extra3.toInt(), "Mining finished and points may given or not.")
        }
    }

    // 2 minutes in milliseconds
    private suspend fun getServerTime(): Long {
        val currentTime = System.currentTimeMillis()
        val lastCachedTime = SpManager.getLong(SpManager.KEY_SERVER_TIME, 0)
        Log.e("isMiningWithin24Hours", "lastCachedTime: $lastCachedTime")
        // Check if the cached server time is within the validity duration
        return if (currentTime - lastCachedTime > Config.serverTimeValidityDuration) {
            try {
                // Initialize the location manager
                if (ContextCompat.checkSelfPermission(
                        MyApp.context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    val locationManager = MyApp.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val getNetTime = GetServerTime(latitude, longitude)
                        val newServerTime = getNetTime.getTime()
                        SpManager.saveLong(SpManager.KEY_SERVER_TIME, newServerTime)
                        newServerTime
                    } else {
                        currentTime // Return current system time as a fallback
                    }
                } else {
                    Log.e("getServerTime", "Location permission not granted")
                    currentTime // Return current system time as a fallback
                }
            } catch (e: Exception) {
                // Handle the exception
                Log.e("getServerTime", "Error fetching server time: ${e.message}")
                currentTime // Return current system time as a fallback
            }
        } else {
            // Retrieve the server time from shared preferences
            lastCachedTime
        }
    }


    /*    private suspend fun getServerTime(): Long {
            val currentTime = System.currentTimeMillis()
            val lastCachedTime = SpManager.getLong(SpManager.KEY_SERVER_TIME, 0)
            Log.e("fetchData111", "lastCachedTime: $lastCachedTime")
            // Check if the cached server time is within the validity duration
            return if (currentTime - lastCachedTime > serverTimeValidityDuration) {
                try {
                    val getNetTime = GetServerTime()
                    val newServerTime = getNetTime.getTime()
                    Log.e("fetchData111", "GetServerTime: ")
                    SpManager.saveLong(SpManager.KEY_SERVER_TIME, newServerTime)
                    newServerTime
                } catch (e: Exception) {
                    // Handle the exception
                    Log.e("getServerTime", "Error fetching server time: ${e.message}")
                    currentTime // Return current system time as a fallback
                }
            } else {
                // Retrieve the server time from shared preferences
                lastCachedTime
            }
        }*/

/*    private suspend fun getServerTime(): Long {
        return try {
            val getNetTime = GetServerTime()
            Log.e("fetchData111", "GetServerTime: ")
            getNetTime.getTime()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }*/
}

data class TimeStatus(val status: Int, val message: String? = null)
