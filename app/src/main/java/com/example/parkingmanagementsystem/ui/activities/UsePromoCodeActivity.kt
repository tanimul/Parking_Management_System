package com.example.parkingmanagementsystem.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivityUsePromoCodeBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast

class UsePromoCodeActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "UsePromoCodeActivity"
    }

    private lateinit var binding: ActivityUsePromoCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsePromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }


        binding.btnSubmit.setOnClickListener {
            SharedPrefUtils().setValue(
                Constants.SharedPref.PROMO_CODE,
                binding.etPromoCode.text.toString()
            )

            toast("Promo Code Save Successfully")
            finish()
        }
    }
}