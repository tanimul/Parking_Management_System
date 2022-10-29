package com.example.parkingmanagementsystem.ui.user.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivityUsePromoCodeBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast
import java.util.HashMap

class UsePromoCodeActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "UsePromoCodeActivity"
    }

    private lateinit var binding: ActivityUsePromoCodeBinding
    var map_month: HashMap<String, Int> = hashMapOf("Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3, "May" to 4, "Jun" to 5)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsePromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }


        binding.btnSubmit.setOnClickListener {
            if(map_month.containsKey(binding.etPromoCode.text.toString())){
                SharedPrefUtils().setValue(
                    Constants.SharedPref.PROMO_CODE,
                    binding.etPromoCode.text.toString()
                )

                toast("Promo Code Save Successfully")
                finish()
            }else{
                toast("Promo Code Invalid")
            }

        }
    }
}