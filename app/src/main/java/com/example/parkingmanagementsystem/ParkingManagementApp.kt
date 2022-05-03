package com.example.parkingmanagementsystem

import android.app.Application
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.KEY_AUTH_TOKEN
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.getSharedPrefInstance

class ParkingManagementApp : Application() {


    override fun onCreate() {
        instance = this
        super.onCreate()
        getSharedPrefInstance().apply {
            authToken = getStringValue(KEY_AUTH_TOKEN, "")
        }
    }


    companion object {
        private lateinit var instance: ParkingManagementApp
        var sharedPrefUtils: SharedPrefUtils? = null
        var authToken = ""
        fun getInstance(): ParkingManagementApp {
            return instance
        }



    }

}