package com.example.parkingmanagementsystem.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.ParkingOwner
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityProfileBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_MANAGEMENT_COLLECTION
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_PARKING_OWNER_COLLECTION
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppBaseActivity() {
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth

    companion object {
        private const val TAG: String = "ProfileActivity"
    }

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        mAuth = FirebaseAuth.getInstance()

        mAuth.currentUser?.let { setUserHeaderInformation(it.uid) }


        if (SharedPrefUtils().getBooleanValue(Constants.SharedPref.IS_LOGGIN) && SharedPrefUtils().getStringValue(
                Constants.SharedPref.PARKING_OWNER_ID
            ) == ""
        ) {
            setManagementInformation()
        } else {
            setParkingInformation()
        }

    }

    private fun setManagementInformation() {
        db.collection(KEY_MANAGEMENT_COLLECTION)
            .document(SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID)).get()
            .addOnSuccessListener { snapshot ->
                val parking_owner = snapshot.toObject(ParkingOwner::class.java)!!
                Log.d(TAG, "User Information: $parking_owner")

                binding.tvName.text = parking_owner?.name
                binding.tvLocation.text = parking_owner?.address
                binding.tvEmail.text = parking_owner?.email
                binding.tvPhoneNumber.text = parking_owner?.phoneNumber
                binding.tvVehicleNumber.visibility = View.GONE
                parking_owner?.let { binding.civUserImage.loadImageFromUrl(it.imageUrl) }
            }

    }

    private fun setParkingInformation() {
        db.collection(KEY_PARKING_OWNER_COLLECTION)
            .document(SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)).get()
            .addOnSuccessListener { snapshot ->
                val parking_owner = snapshot.toObject(ParkingOwner::class.java)!!
                Log.d(TAG, "User Information: $parking_owner")

                binding.tvName.text = parking_owner?.name
                binding.tvLocation.text = parking_owner?.address
                binding.tvEmail.text = parking_owner?.email
                binding.tvPhoneNumber.text = parking_owner?.phoneNumber
                binding.tvVehicleNumber.visibility = View.GONE
                parking_owner?.let { binding.civUserImage.loadImageFromUrl(it.imageUrl) }
            }

    }

    private fun setUserHeaderInformation(uid: String) {
        db.collection(Constants.FirebaseKeys.KEY_USERS_COLLECTION)
            .document(uid).get().addOnSuccessListener { snapshot ->
                val user_info = snapshot.toObject<User>()
                Log.d(TAG, "User Information: $user_info")

                binding.tvName.text = user_info?.name
                binding.tvLocation.text = user_info?.address
                binding.tvEmail.text = user_info?.email
                binding.tvPhoneNumber.text = user_info?.phoneNumber
                binding.tvVehicleNumber.text = user_info?.vehicle_number
                user_info?.let { binding.civUserImage.loadImageFromUrl(it.imageUrl) }
            }


    }
}