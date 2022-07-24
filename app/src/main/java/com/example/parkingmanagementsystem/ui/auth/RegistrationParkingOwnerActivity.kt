package com.example.parkingmanagementsystem.ui.auth

import android.R
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.example.parkingmanagementsystem.databinding.ActivityRegistrationParkingOwnerBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.main.HomeActivity
import com.example.parkingmanagementsystem.ui.main.HomeManagementActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.Variables
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RegistrationParkingOwnerActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "RegParOwnerActivity"
    }

    private lateinit var binding: ActivityRegistrationParkingOwnerBinding

    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private var imageUrl: String? = ""
    private var gender: String? = null

    private var phone_number: String = ""
    private val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationParkingOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Spinner
        genderTypes()

        mAuth = FirebaseAuth.getInstance()
        storageReference =
            FirebaseStorage.getInstance().reference.child("managementImages")


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
            R.layout.simple_spinner_item,
            resources.getStringArray(com.example.parkingmanagementsystem.R.array.gender)
        )
        gender_types.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
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
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        gender = binding.spinnerGender.selectedItem.toString()


        //set user data
        Variables.parkingOwner._id = phone
        Variables.parkingOwner.name = name
        Variables.parkingOwner.email = email
        Variables.parkingOwner.address = address
        Variables.parkingOwner.phoneNumber = phone
        Variables.parkingOwner.password = password
        Variables.parkingOwner.gender = gender!!
        Variables.parkingOwner.imageUrl = imageUrl!!
        Variables.parkingOwner.updatedAt = System.currentTimeMillis()



        db.collection(Constants.FirebaseKeys.KEY_MANAGEMENT_COLLECTION)
            .document(phone)
            .set(Variables.parkingOwner).addOnSuccessListener {
                //hide progressbar
                showProgress(false)
                Log.d(TAG, "Profile updated successfully ")
                toast("Registration successfully")
                SharedPrefUtils().setValue(Constants.SharedPref.IS_LOGGIN, true)
                SharedPrefUtils().setValue(Constants.SharedPref.FULL_NAME, name)
                SharedPrefUtils().setValue(Constants.SharedPref.PHONE_NUMBER, phone)
                SharedPrefUtils().setValue(Constants.SharedPref.IMAGE_URL, imageUrl!!)
                launchActivity<HomeManagementActivity>()
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
                || binding.etPhone.text.toString().isNotEmpty()
                || binding.etPassword.text.toString().isNotEmpty()
                || imageUri != null)
    }
}