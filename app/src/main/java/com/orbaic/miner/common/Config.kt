package com.orbaic.miner.common

import java.util.concurrent.TimeUnit

object Config {
    var serverAllowedTimeDifference = 30 //minutes

    val serverTimeValidityDuration = TimeUnit.MINUTES.toMillis(2)

    val totalHours = "24:00:00"
    var hourRate = "0.045"

    var miningRewardedTokenCode = "SHIB"
    var miningRewardedBonusToken: Long = 3000

    var quizRewardedTokenCode = "SHIB"
    var quizRewardedBonusToken: Long = 2000

    var miningCountReward: Int = 24


    var correctQuizReward: Int = 1
    var referBonusReward: Int = 3
}