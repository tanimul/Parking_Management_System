package com.example.parkingmanagementsystem.utils.extentions

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.parkingmanagementsystem.ParkingManagementApp.Companion.getInstance

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkAvailable(): Boolean {
    val info = getInstance().getConnectivityManager().activeNetworkInfo
    return info != null && info.isConnected
}
