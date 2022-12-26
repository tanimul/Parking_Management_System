package com.example.parkingmanagementsystem.ui.user.auth

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivityRegistrationBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.user.main.HomeActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.Variables.user
import com.example.parkingmanagementsystem.utils.Variables.userId
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RegistrationActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "RegistrationActivity"
    }

    private lateinit var binding: ActivityRegistrationBinding
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private var imageUrl: String? = ""
    private var gender: String? = null

    private var phone_number: String = ""
    private val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.getStringExtra("phone_number") != null) {
            phone_number = intent.getStringExtra("phone_number")!!
        }

        //Spinner
        genderTypes()

        mAuth = FirebaseAuth.getInstance()
        storageReference =
            FirebaseStorage.getInstance().reference.child("userImages").child(userId)


        binding.civUserImage.setOnClickListener {
            mGetContent.launch("image/*")
        }
        binding.btnRegister.setOnClickListener {
            if (registerValidation()) {
                saveInfo()
            } else {
                toast("Please fill the all field Correctly")
            }
        }
        binding.ibDropdown.setOnClickListener {
            binding.spinnerGender.performClick()
        }


    }

    private fun genderTypes() {
        val gender_types = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.gender)
        )
        gender_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = gender_types

    }

    //Get image extention
    private fun GetFileExtention(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> // Handle the returned Uri
        imageUri = uri
        binding.civUserImage.setImageURI(imageUri)
    }

    private fun saveInfo() {
        showProgress(true)
        if (imageUri != null) {
            Log.d(TAG, "saveInfo: ")
            storageReference = storageReference.child(
                "" + System.currentTimeMillis() + "." + GetFileExtention(imageUri)
            )
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        Log.d(TAG, "Successfully added")
                        registerUserProfile()

                    }
                }.addOnFailureListener { e ->
                    showProgress(false)
                    Log.d(TAG, "error: " + e.message)
                    toast("$e.message")
                }
        }
    }

    private fun registerUserProfile() {
        //get data
        val name = binding.etFullName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val nid_number = binding.etNid.text.toString().trim()
        val vehicle_number = binding.etVehicleNumber.text.toString().trim()
        gender = binding.spinnerGender.selectedItem.toString()

        //set user data
        user._id = userId
        user.name = name
        user.email = email
        user.phoneNumber = phone_number
        user.address = address
        user.nid_number = nid_number
        user.vehicle_number = vehicle_number
        user.gender = gender!!
        user.imageUrl = imageUrl!!
        user.updatedAt = System.currentTimeMillis()

        db.collection(Constants.FirebaseKeys.KEY_USERS_COLLECTION)
            .document(userId)
            .set(user).addOnSuccessListener {
                //hide progressbar
                showProgress(false)
                Log.d(TAG, "Profile updated successfully ")
                toast("Profile updated successfully")
                SharedPrefUtils().setValue(Constants.SharedPref.USERS_ID, user._id)
                launchActivity<HomeActivity>()
                finish()

            }.addOnFailureListener {
                showProgress(false)
                Log.d(TAG, "${it.localizedMessage!!} ")
                toast(it.localizedMessage!!)
            }
    }

    private fun registerValidation(): Boolean {
        return (binding.etFullName.text.toString().isNotEmpty()
                || binding.etEmail.text.toString().isNotEmpty()
                || binding.etAddress.text.toString().isNotEmpty()
                || binding.etVehicleNumber.text.toString().isNotEmpty()
                || binding.etNid.text.toString().isNotEmpty()
                || binding.etNid.text.toString().length==13
                || imageUri != null)
    }

}