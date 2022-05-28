package com.example.parkingmanagementsystem.ui.auth

import android.os.Bundle
import com.example.parkingmanagementsystem.databinding.ActivityLoginBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity

class LoginActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}