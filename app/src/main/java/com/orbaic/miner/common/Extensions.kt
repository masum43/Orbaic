package com.orbaic.miner.common

import android.util.Log
import android.widget.Toast
import com.orbaic.miner.MyApp
import java.util.Locale

fun Number.roundTo(
    numFractionDigits: Int = 4
) = "%.${numFractionDigits}f".format(this, Locale.ENGLISH).toDouble()


fun String.roundTo(
    numFractionDigits: Int = 4
) = "%.${numFractionDigits}f".format(this.toDouble(), Locale.ENGLISH)


fun String.toast(length: Int = 0) = Toast.makeText(MyApp.context, this, length).show()

fun String.checkMiningStatusTeam(): String {
    val now = System.currentTimeMillis()
    val miningStartTimeLong = this.toLong()
    val timeElapsed = now - miningStartTimeLong
    Log.e("checkMiningStatus", "now: $now")
    Log.e("checkMiningStatus", "miningStartTimeLong: $miningStartTimeLong")
    Log.e("checkMiningStatus", "timeElapsed: $timeElapsed")
    return if (timeElapsed >= 24 * 60 * 60 * 1000) {
        // If more than 24 hours have elapsed, do something
        // Your code here
        Constants.STATUS_OFF
    } else {
        // If less than 24 hours have elapsed, do something else
        // Your code here
        Constants.STATUS_ON
    }
}