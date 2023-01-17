package ir.rev.foodMaker.utils

import android.content.Context
import android.net.ConnectivityManager
import ir.rev.foodMaker.FoodPlugin

internal fun isInternetAvailable(): Boolean {
    val networkInfo =
        (FoodPlugin.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}