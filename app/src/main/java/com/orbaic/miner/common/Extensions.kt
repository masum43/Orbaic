package com.orbaic.miner.common

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.orbaic.miner.MyApp
import com.orbaic.miner.R
import com.orbaic.miner.home.MyRewardedTokenItem
import org.json.JSONArray
import java.util.Locale


fun Number.roundTo(): String {
    return String.format(Locale.ENGLISH, "%.5f", this)
    /*    val roundedValue = String.format(Locale.ENGLISH, "%.${numFractionDigits}f", this)
    return DecimalFormat("#.${"#".repeat(numFractionDigits)}").format(roundedValue.toDouble())*/
}

fun String.parseNumber(): Double {
    return this.replace(",", ".").toDouble()
}


fun String.toast(length: Int = 0) = Toast.makeText(MyApp.context, this, length).show()

fun View.show() {
    this.visibility = View.VISIBLE
}
fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

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

fun Context.readRawResourceAsString(resourceId: Int): String {
    return resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
}

fun getRewardTokenFromJson(rewardedTokenCode: String): MyRewardedTokenItem {
    val jsonText = MyApp.context.readRawResourceAsString(R.raw.reward_tokens)
    val rewardToken = MyRewardedTokenItem()

    val jsonArray = JSONArray(jsonText)
    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        if (jsonObject.getString("code") == rewardedTokenCode) {
            rewardToken.id = jsonObject.getInt("id").toLong()
            rewardToken.name = jsonObject.getString("name")
            rewardToken.code = jsonObject.getString("code")
            rewardToken.balance = jsonObject.getString("balance")
            rewardToken.icon = jsonObject.getString("icon")
            break
        }
    }
    return rewardToken
}