package com.orbaic.miner.common

import android.health.connect.datatypes.units.Length
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