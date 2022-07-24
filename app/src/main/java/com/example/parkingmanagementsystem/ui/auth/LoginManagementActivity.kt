package com.example.parkingmanagementsystem.ui.auth

import android.os.Bundle
import android.util.Log
import com.example.parkingmanagementsystem.data.model.response.ParkingOwner
import com.example.parkingmanagementsystem.databinding.ActivityLoginManagementBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.main.HomeManagementActivity
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_MANAGEMENT_COLLECTION
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.FULL_NAME
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.IMAGE_URL
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.IS_LOGGIN
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.PHONE_NUMBER
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginManagementActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "LoginManagementActivity"
    }

    val db = Firebase.firestore
    private lateinit var binding: ActivityLoginManagementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegisterParkingOwner.setOnClickListener {
            launchActivity<RegistrationParkingOwnerActivity>()
        }


        binding.btnLogin.setOnClickListener {
            if (checkValidation()) {
                checkInfo()
            } else {
                toast("Please fill the all field Correctly")
            }
        }

    }

    private fun checkValidation(): Boolean {
        return (binding.etPhone.text.toString().isNotEmpty()
                || binding.etPassword.text.toString().isNotEmpty())
    }

    private fun checkInfo() {

        showProgress(true)
        db.collection(KEY_MANAGEMENT_COLLECTION).document(binding.etPhone.text.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    showProgress(false)
                    val parking_owner = snapshot.toObject(ParkingOwner::class.java)!!
                    if (parking_owner.password == binding.etPassword.text.toString()) {
                        SharedPrefUtils().setValue(IS_LOGGIN, true)
                        SharedPrefUtils().setValue(FULL_NAME, parking_owner.name)
                        SharedPrefUtils().setValue(PHONE_NUMBER, parking_owner.phoneNumber)
                        SharedPrefUtils().setValue(IMAGE_URL, parking_owner.imageUrl)
                        toast("Login Successfully")
                        launchActivity<HomeManagementActivity>()
                    }
                    else{
                        showProgress(false)
                        toast("Wrong Phone number or Password")
                    }

                }else{
                    showProgress(false)
                    toast("User Not Found")
                }
            }.addOnFailureListener{
                showProgress(false)
                toast(it.localizedMessage!!)
            }
    }
}