package com.example.parkingmanagementsystem.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "RegistrationActivity"
    }

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)





    }
}