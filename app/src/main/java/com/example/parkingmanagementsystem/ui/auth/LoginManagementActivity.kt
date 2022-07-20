package com.example.parkingmanagementsystem.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.databinding.ActivityLoginManagementBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.extentions.launchActivity

class LoginManagementActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "LoginManagementActivity"
    }

    private lateinit var binding: ActivityLoginManagementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegisterParkingOwner.setOnClickListener {
            launchActivity<RegistrationCarOwnerActivity>()
        }
    }
}