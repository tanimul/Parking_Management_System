package com.example.parkingmanagementsystem.ui.parking_add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parkingmanagementsystem.data.model.response.ParkingInfo
import com.example.parkingmanagementsystem.databinding.ActivityParkingAddBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_PARKING_INFO
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ParkingAddActivity : AppBaseActivity() {
    private lateinit var binding: ActivityParkingAddBinding
    private val TAG: String = "ParkingAddActivity"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    val db = Firebase.firestore
    private lateinit var image_uri: Uri
    private var u_id: String = ""
    private var priority: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var key: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkingAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().getReference("parking_images")



        binding.radioGroup.setOnCheckedChangeListener { p0, p1 ->
            val radioButton: View = binding.radioGroup.findViewById(p1)
            val index: Int = binding.radioGroup.indexOfChild(radioButton)

              if(index == 0) {
                  binding.layoutTie.visibility = View.GONE
            }else{
                  binding.layoutTie.visibility = View.VISIBLE
              }
        }


        binding.ivParking.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                mGetContent.launch("image/*")
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }

        }

        binding.btnAddParking.setOnClickListener {
//            if (image_uri != null && binding.etPlace.text.toString()
//                    .isNotEmpty() && binding.tvAddress.text == null
//                && binding.etTotalSpace.text.toString()
//                    .isNotEmpty() && latitude != 0.0 && longitude != 0.0
//            ) {
            placeStore()
//            } else {
//                toast("Please fill the all filed correctly.")
//            }
        }
        binding.tvAddress.setOnClickListener {
            val intent = Intent(this, AddressPickupActivity::class.java)
            addressPickupActResult.launch(intent)
        }

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    private val addressPickupActResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {


        }


    //Get image extention
    fun GetFileExtention(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> // Handle the returned Uri
        if (uri != null) {

            Log.d(TAG, "uri:$uri ")
            image_uri = uri
            binding.ivParking.setImageURI(image_uri)

        }

    }

    private fun placeStore() {
        showProgress(true)
        u_id = if (SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID) == "") {
            SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID)
        } else {
            SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)
        }

        priority = if (binding.radio1.isChecked) {
            "Free"
        } else {
            "Premium"
        }

        if (image_uri != null) {
            storageReference =
                storageReference.child(
                    "" + System.currentTimeMillis() + "." + GetFileExtention(
                        image_uri
                    )
                )


            storageReference.putFile(image_uri)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val url = uri.toString()
                        key = System.currentTimeMillis().toString()
                        val place_info = ParkingInfo(
                            u_id,
                            key,
                            binding.etPlace.text.toString(),
                            binding.tvAddress.text.toString(),
                            latitude,
                            longitude,
                            priority,
                            "" + binding.etCostPerHour.text.toString(),
                            binding.etTotalSpace.text.toString(),
                            url,
                        )

                        db.collection(KEY_PARKING_INFO)
                            .document("" + key).set(place_info)
                            .addOnSuccessListener {
                                //hide progressbar
                                showProgress(false)
                                toast("Successfully Data Added.")
                                finish()
                            }.addOnFailureListener {
                                showProgress(false)
                                Log.d(TAG, "imageStore: ${it.localizedMessage}")
                                toast(it.localizedMessage!!)
                            }

                    }
                }.addOnFailureListener { e ->
                    showProgress(false)
                    Log.d(TAG, "addOnFailureListener: " + e.message)
                    toast("" + e.message)
                }
        }
    }
}