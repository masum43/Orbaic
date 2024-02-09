package com.orbaic.miner.homeNew

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.orbaic.miner.MyApp
import com.orbaic.miner.common.GetNetTime
import kotlinx.coroutines.tasks.await
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
    var name: String = "",
    var phone: String = "",
    var point: String = "",
    var referral: String = "",
    var referralButton: String = "",
    var referredBy: String = "",
    var referredByCode: String = "",
    var type: String = ""
) {
    suspend fun isWithin24Hours(): TimeStatus {
        if (miningStartTime.isEmpty()) {
            return TimeStatus(0, "Mining start time is empty.")
        }

        val serverTime = getServerTime()
        val currentTime = System.currentTimeMillis()
        val timeDifference = abs(currentTime - serverTime)

        // Convert time difference to days, hours, minutes, and seconds
        val days = timeDifference / (1000 * 60 * 60 * 24)
        val hours = (timeDifference / (1000 * 60 * 60)) % 24
        val minutes = (timeDifference / (1000 * 60)) % 60
        val seconds = (timeDifference / 1000) % 60

        // Check if the time difference exceeds 5 minutes
        if (timeDifference > 5 * 60 * 1000) {
            return TimeStatus(2, "Time difference between server and device is too large: $days days, $hours hours, $minutes minutes, $seconds seconds.")
        }

        val miningStartTimeTimestamp = miningStartTime.toLongOrNull() ?: return TimeStatus(0, "Invalid mining start time format.")
        val diff = abs(serverTime - miningStartTimeTimestamp)
        val diffHours = diff / (1000 * 60 * 60)

        return if (diffHours < 24) {
            TimeStatus(1)
        } else {
            TimeStatus(0, "Mining start time is not within 24 hours.")
        }
    }

    private suspend fun getServerTime(): Long {
        return try {
            val getNetTime = GetNetTime()
            getNetTime.getNetTime(MyApp.context)
        } catch (e: Exception) {
            // Error handling
            System.currentTimeMillis() // Fall back to device time in case of failure
        }
    }

/*    private suspend fun getServerTime(): Long {
        return try {
            val serverTimeSnapshot = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset").get().await()
            val offset = serverTimeSnapshot.getValue(Long::class.java) ?: 0
            Log.e("remainingTime", "offset: $offset")
            System.currentTimeMillis() + offset
        } catch (e: Exception) {
            Log.e("remainingTime", "getServerTime: ${e.localizedMessage}")
            System.currentTimeMillis() // Fall back to device time in case of failure

        }
    }*/
}

data class TimeStatus(val status: Int, val message: String? = null)
