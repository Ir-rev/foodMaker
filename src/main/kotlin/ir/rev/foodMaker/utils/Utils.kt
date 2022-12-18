package ir.rev.foodMaker.utils

import java.net.InetAddress

internal fun isInternetAvailable(): Boolean {
    return try {
        val ipAddr: InetAddress = InetAddress.getByName("google.com")
        !ipAddr.equals("")
    } catch (e: Exception) {
        false
    }
}