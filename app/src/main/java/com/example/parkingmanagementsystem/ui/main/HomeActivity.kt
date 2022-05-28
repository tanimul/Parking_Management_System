package com.example.parkingmanagementsystem.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}