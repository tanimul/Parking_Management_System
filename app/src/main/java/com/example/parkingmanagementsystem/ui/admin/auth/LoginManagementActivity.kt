package com.example.parkingmanagementsystem.ui.admin.auth

import android.os.Bundle
import android.util.Log
import com.example.parkingmanagementsystem.data.model.response.ParkingOwner
import com.example.parkingmanagementsystem.databinding.ActivityLoginManagementBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.admin.HomeManagementActivity
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_MANAGEMENT_COLLECTION
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_PARKING_OWNER_COLLECTION
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.FULL_NAME
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.IMAGE_URL
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.IS_LOGGIN
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.IS_MANAGEMENT
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.MANAGEMENT_ID
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.PARKING_OWNER_ID
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
    var loginStatus=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegisterParkingOwner.setOnClickListener {
            launchActivity<RegistrationParkingOwnerActivity>()
        }


        binding.btnLogin.setOnClickListener {
            if (checkValidation()) {
                if (binding.radio1.isChecked) {
                  checkAdminInfo()
                } else {
                    checkParkingOwnerInfo()
                }

            } else {
                toast("Please fill the all field Correctly")
            }
        }

    }

    private fun checkValidation(): Boolean {
        return (binding.etPhone.text.toString().isNotEmpty()
                || binding.etPassword.text.toString().isNotEmpty())
    }

    private fun checkAdminInfo() {
        showProgress(true)
        db.collection(KEY_MANAGEMENT_COLLECTION).get()
            .addOnSuccessListener { snapshot ->
                Log.d(TAG, "checkParkingOwnerInfo: " + snapshot.size())
                if (snapshot.size() != 0) {
                    showProgress(false)
                    for (snapshot1 in snapshot) {
                        val parking_owner = snapshot1.toObject(ParkingOwner::class.java)
                        if (parking_owner.phoneNumber == binding.etPhone.text.toString() && parking_owner.password == binding.etPassword.text.toString()) {
                            SharedPrefUtils().setValue(IS_LOGGIN, true)
                            SharedPrefUtils().setValue(IS_MANAGEMENT, true)
                            SharedPrefUtils().setValue(FULL_NAME, parking_owner.name)
                            SharedPrefUtils().setValue(PHONE_NUMBER, parking_owner.phoneNumber)
                            SharedPrefUtils().setValue(MANAGEMENT_ID, parking_owner._id)
                            SharedPrefUtils().setValue(PARKING_OWNER_ID, "")
                            SharedPrefUtils().setValue(IMAGE_URL, parking_owner.imageUrl)
                            loginStatus=true
                            break
                        } else {
                            loginStatus=false

                        }
                    }


                    if(loginStatus){
                        toast("Login Successfully")
                        launchActivity<HomeManagementActivity>()
                        finish()
                    }else{
                        showProgress(false)
                        toast("Wrong Phone number or Password")
                    }

                } else {
                    showProgress(false)
                    toast("User Not Found")
                }
            }.addOnFailureListener {
                showProgress(false)
                toast(it.localizedMessage!!)
            }
    }

    private fun checkParkingOwnerInfo() {

        showProgress(true)
        db.collection(KEY_PARKING_OWNER_COLLECTION).get()
            .addOnSuccessListener { snapshot ->
                Log.d(TAG, "checkParkingOwnerInfo: " + snapshot.size())
                if (snapshot.size() != 0) {
                    showProgress(false)
                    for (snapshot1 in snapshot) {
                        val parking_owner = snapshot1.toObject(ParkingOwner::class.java)
                        if (parking_owner.phoneNumber == binding.etPhone.text.toString() && parking_owner.password == binding.etPassword.text.toString()) {
                            SharedPrefUtils().setValue(IS_LOGGIN, true)
                            SharedPrefUtils().setValue(IS_MANAGEMENT, false)
                            SharedPrefUtils().setValue(FULL_NAME, parking_owner.name)
                            SharedPrefUtils().setValue(PHONE_NUMBER, parking_owner.phoneNumber)
                            SharedPrefUtils().setValue(PARKING_OWNER_ID, parking_owner._id)
                            SharedPrefUtils().setValue(MANAGEMENT_ID, "")
                            SharedPrefUtils().setValue(IMAGE_URL, parking_owner.imageUrl)
                            loginStatus=true
                            break
                        } else {
                            loginStatus=false
                        }
                    }
                    if(loginStatus){
                        toast("Login Successfully")
                        launchActivity<HomeManagementActivity>()
                        finish()
                    }else{
                        showProgress(false)
                        toast("Wrong Phone number or Password")
                    }

                } else {
                    showProgress(false)
                    toast("User Not Found")
                }
            }.addOnFailureListener {
                showProgress(false)
                toast(it.localizedMessage!!)
            }
    }
}