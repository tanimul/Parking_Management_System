package com.example.parkingmanagementsystem.utils

import com.example.parkingmanagementsystem.data.model.response.ParkingOwner
import com.example.parkingmanagementsystem.data.model.response.User
import com.google.firebase.auth.FirebaseAuth

object Variables {
    var userId = FirebaseAuth.getInstance().uid ?: ""
    var user = User()
    var parkingOwner = ParkingOwner()
}