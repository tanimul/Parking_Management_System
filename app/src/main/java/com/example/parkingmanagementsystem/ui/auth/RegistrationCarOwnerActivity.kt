package com.example.parkingmanagementsystem.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivityRegistrationCarOwnerBinding

class RegistrationCarOwnerActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "RegistrationCarOwnerActivity"
    }

    private lateinit var binding: ActivityRegistrationCarOwnerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegistrationCarOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}