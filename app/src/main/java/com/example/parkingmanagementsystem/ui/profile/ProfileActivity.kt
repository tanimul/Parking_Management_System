package com.example.parkingmanagementsystem.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.parkingmanagementsystem.data.model.response.ParkingOwner
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityProfileBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_MANAGEMENT_COLLECTION
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_PARKING_OWNER_COLLECTION
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_USERS_COLLECTION
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

       // mAuth.currentUser?.let { setUserHeaderInformation(it.uid) }

        if (mAuth.currentUser != null) {
            setUserHeaderInformation(SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID))
        }
        else if (SharedPrefUtils().getBooleanValue(Constants.SharedPref.IS_LOGGIN) && SharedPrefUtils().getStringValue(
                Constants.SharedPref.PARKING_OWNER_ID
            ) == ""
        ) {
            setManagementInformation()
        } else {
            setParkingOwnerInformation()
        }
        binding.ibBack.setOnClickListener {
            onBackPressed()
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

    private fun setParkingOwnerInformation() {
        db.collection(KEY_PARKING_OWNER_COLLECTION)
            .document(SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)).get()
            .addOnSuccessListener { snapshot ->
                val parking_owner = snapshot.toObject(ParkingOwner::class.java)!!
                Log.d(TAG, "User Information: $parking_owner")

                binding.tvName.text = parking_owner?.name
                binding.tvLocation.text = parking_owner?.address
                binding.tvEmail.text = "Email: "+parking_owner?.email
                binding.tvPhoneNumber.text = "Phone: "+parking_owner?.phoneNumber
                binding.tvVehicleNumber.visibility = View.GONE
                binding.tvVehicleNumber.visibility = View.GONE
                parking_owner?.let { binding.civUserImage.loadImageFromUrl(it.imageUrl) }
            }

    }

    private fun setUserHeaderInformation(uid: String) {
        db.collection(KEY_USERS_COLLECTION)
            .document(uid).get().addOnSuccessListener { snapshot ->
                val user_info = snapshot.toObject<User>()
                Log.d(TAG, "User Information: $user_info")

                binding.tvName.text = user_info?.name
                binding.tvLocation.text = user_info?.address
                binding.tvEmail.text = "Email: "+user_info?.email
                binding.tvPhoneNumber.text = "Phone: "+user_info?.phoneNumber
                binding.tvVehicleNumber.text = "Vehicle No: "+user_info?.vehicle_number
                binding.tvNid.text = "Nid: "+user_info?.nid_number
                binding.tvVehicleNumber.visibility=View.VISIBLE
                binding.tvNid.visibility=View.VISIBLE
                user_info?.let { binding.civUserImage.loadImageFromUrl(it.imageUrl) }
            }


    }
}