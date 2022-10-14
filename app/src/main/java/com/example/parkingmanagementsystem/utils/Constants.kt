package com.example.parkingmanagementsystem.utils

object Constants {

    object SharedPref {
        const val myPreferences = "MyPreferences"
        const val KEY_AUTH_TOKEN = "authToken"
        const val IS_LOGGIN = "isLogin"
        const val IS_MANAGEMENT = "isManagement"
        const val PHONE_NUMBER = "phoneNumber"
        const val FULL_NAME = "fullName"
        const val IMAGE_URL = "imageUrl"
        const val PARKING_OWNER_ID = "parkingOwnerId"
        const val MANAGEMENT_ID = "managementId"
        const val PROMO_CODE = "promoCode"
    }
    object FirebaseKeys {
        const val KEY_USERS_COLLECTION = "Users"
        const val KEY_MANAGEMENT_COLLECTION = "Managements"
        const val KEY_PARKING_OWNER_COLLECTION = "ParkingOwner"
        const val KEY_NOTIFICATION_INFO = "notifications"
        const val KEY_PARKING_INFO = "parking_info"
        const val KEY_MONTHLY_PARKING_INFO = "monthly_parking_info"
    }

}