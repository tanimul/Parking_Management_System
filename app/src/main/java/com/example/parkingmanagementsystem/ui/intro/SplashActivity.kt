package com.example.parkingmanagementsystem.ui.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.parkingmanagementsystem.databinding.ActivitySplashBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.auth.LoginActivity
import com.example.parkingmanagementsystem.ui.main.HomeActivity
import com.example.parkingmanagementsystem.ui.main.HomeManagementActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "SplashActivity"
    }

    private lateinit var binding: ActivitySplashBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init
        mAuth = FirebaseAuth.getInstance()

        //Hold 3 Sec and go to Activity
        Handler(Looper.getMainLooper()).postDelayed({
            if (mAuth.currentUser != null) {
                Log.d(TAG, "onCreate: Current User Not Null.")
                launchActivity<HomeActivity>()
                finish()
            } else if (SharedPrefUtils().getBooleanValue(Constants.SharedPref.IS_LOGGIN, false)) {
                Log.d(TAG, "onCreate: Current Management Not Null.")
                launchActivity<HomeManagementActivity>()
                finish()
            } else {
                Log.d(TAG, "onCreate: Current User Null.")
                launchActivity<LoginActivity>()
                finish()
            }
        }, 1000)
    }
}