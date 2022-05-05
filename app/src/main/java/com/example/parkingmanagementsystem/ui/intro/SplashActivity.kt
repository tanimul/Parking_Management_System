package com.example.parkingmanagementsystem.ui.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val TAG: String = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Hold 3 Sec and go to Activity by get value from SharedPreferences
        Handler(Looper.getMainLooper()).postDelayed({
            finish()

        }, 3000)
    }
}