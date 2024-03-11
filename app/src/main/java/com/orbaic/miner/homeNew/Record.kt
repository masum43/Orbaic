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

data class Record(
    var totalMiningPoints: Any = "",
    var totalBoostPoints: Any = "",
    var totalRefPoints: Any = "",
    var totalQuizPoints: Any = "",
    var total_qz_count: Any = "",
    var total_mining_count: Any = "",
)

