package com.example.parkingmanagementsystem.ui.booking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.databinding.ActivityAddBookingBinding

class AddBookingActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "AddBookingActivity"
    }

    private lateinit var binding: ActivityAddBookingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}