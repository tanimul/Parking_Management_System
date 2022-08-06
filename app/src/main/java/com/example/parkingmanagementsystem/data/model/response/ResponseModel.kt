package com.example.parkingmanagementsystem.data.model.response

import java.io.Serializable

data class User(
    var _id: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var imageUrl: String = "",
    var email: String = "",
    var gender: String = "",
    var nid_number: String = "",
    var vehicle_number: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) : Serializable

data class ParkingOwner(
    var _id: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var imageUrl: String = "",
    var email: String = "",
    var gender: String = "",
    var password: String = "",
    var con_password: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
) : Serializable


data class NotificationInfo(
    val purpose: String = "",
    val id: String = "",
    val time: Long = 0L,
) : Serializable

data class ParkingInfo(
    val uploaderId: String = "",
    val key: String = "",
    val placeName: String = "",
    val placeAddress: String = "",
    val placeLatitude: Double = 0.0,
    val placeLongitude: Double = 0.0,
    val priority: String = "",
    val ultimateCostPerHour: String = "",
    val totalParkingSpace: String = "",
    val placeUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
):Serializable