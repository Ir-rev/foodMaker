package ir.rev.foodMaker.utils

import ir.rev.foodMaker.models.Food
import java.net.InetAddress
import java.util.UUID
import kotlin.random.Random

internal fun isInternetAvailable(): Boolean {
    return try {
        val ipAddr: InetAddress = InetAddress.getByName("google.com")
        !ipAddr.equals("")
    } catch (e: Exception) {
        false
    }
}