package com.example.parkingmanagementsystem.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityProfileBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
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

        mAuth.currentUser?.let { setHeaderInformation(it.uid) }


    }

    private fun setHeaderInformation(uid: String) {
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