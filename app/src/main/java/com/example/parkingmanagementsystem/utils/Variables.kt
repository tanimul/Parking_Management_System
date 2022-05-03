package com.example.parkingmanagementsystem.utils

import com.google.firebase.auth.FirebaseAuth

object Variables {
    var userId = FirebaseAuth.getInstance().uid ?: ""
}