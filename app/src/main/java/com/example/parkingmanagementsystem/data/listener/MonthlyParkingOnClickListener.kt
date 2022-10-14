package com.example.parkingmanagementsystem.data.listener
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo

interface MonthlyParkingOnClickListener {
    fun onItemClick(monthlyParkingInfo: MonthlyParkingInfo, position: Int)
}