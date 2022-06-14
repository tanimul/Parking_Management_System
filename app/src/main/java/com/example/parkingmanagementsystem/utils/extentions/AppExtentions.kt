package com.example.parkingmanagementsystem.utils.extentions

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.parkingmanagementsystem.ParkingManagementApp
import com.example.parkingmanagementsystem.ParkingManagementApp.Companion.getInstance
import com.example.parkingmanagementsystem.R
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
fun ImageView.loadImageFromUrl(
    aImageUrl: String,
    aPlaceHolderImage: Int = R.drawable.placeholder_image,
    aErrorImage: Int = R.drawable.placeholder_image
) {
    try {
        if (!aImageUrl.checkIsEmpty()) {
            Glide.with(getInstance()).load(aImageUrl).placeholder(aPlaceHolderImage)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int) {
    Glide.with(getInstance()).load(aPlaceHolderImage)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(this)
}