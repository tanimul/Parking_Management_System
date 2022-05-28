package com.example.parkingmanagementsystem.ui.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivitySplashBinding
import com.example.parkingmanagementsystem.ui.auth.LoginActivity
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val TAG: String = "SplashActivity"
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
                launchActivity<LoginActivity>()
                finish()
            } else {
                launchActivity<LoginActivity>()
                finish()
            }
        }, 3000)
    }
}