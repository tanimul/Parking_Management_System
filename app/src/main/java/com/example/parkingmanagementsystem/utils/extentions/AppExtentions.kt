package com.example.parkingmanagementsystem.utils.extentions

import android.content.Context
import android.content.Intent
import com.example.parkingmanagementsystem.ParkingManagementApp
import com.example.parkingmanagementsystem.utils.SharedPrefUtils

inline fun <reified T : Any> Context.launchActivity() {
    startActivity(Intent(this, T::class.java))
}
fun getSharedPrefInstance(): SharedPrefUtils {
    return if (ParkingManagementApp.sharedPrefUtils == null) {
        ParkingManagementApp.sharedPrefUtils = SharedPrefUtils()
        ParkingManagementApp.sharedPrefUtils!!
    } else {
        ParkingManagementApp.sharedPrefUtils!!
    }
}