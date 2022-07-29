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


