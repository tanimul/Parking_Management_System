package com.example.parkingmanagementsystem.ui.parking_add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo
import com.example.parkingmanagementsystem.data.model.response.ParkingInfo
import com.example.parkingmanagementsystem.databinding.ActivityMonthlyParkingAddBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.Serializable

class MonthlyParkingAddActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "MonthlyParAddAct"
    }

    private lateinit var binding: ActivityMonthlyParkingAddBinding
    private lateinit var storageReference: StorageReference
    val db = Firebase.firestore
    private lateinit var image_uri: Uri
    private var u_id: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var key: String = ""
    private var month: String = ""
    private var time: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyParkingAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().getReference("parking_images")


        //Spinner
        itemTypes()

        binding.ibDropdownMonth.setOnClickListener {
            binding.spinnerMonth.performClick()
        }
        binding.ibDropdownSlot.setOnClickListener {
            binding.spinnerSlot.performClick()
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

            placeStore()

        }

        binding.tvAddress.setOnClickListener {
            val intent = Intent(this, AddressPickupActivity::class.java)
            addressPickupActResult.launch(intent)
        }

    }
    private val addressPickupActResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            if (it.resultCode == RESULT_OK) {
                binding.tvAddress.text = it.data?.getStringExtra("addressName")
                latitude = it.data!!.getDoubleExtra("lat", 0.0)
                longitude = it.data!!.getDoubleExtra("long", 0.0)
                Log.d(TAG, "$latitude - $longitude: ")
            }
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

    private fun itemTypes() {
        val month_types = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.month)
        )
        val slot_types = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.slot)
        )
        month_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        slot_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = month_types
        binding.spinnerSlot.adapter = slot_types
    }


    private fun placeStore() {
        showProgress(true)
        u_id = if (SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID) == "") {
            SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID)
        } else {
            SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)
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
                        month=binding.spinnerMonth.selectedItem.toString()
                        time=binding.spinnerSlot.selectedItem.toString()

                        val place_info = MonthlyParkingInfo(
                            u_id,
                            key,
                            binding.etPlace.text.toString(),
                            binding.tvAddress.text.toString(),
                            latitude,
                            longitude,
                            month,
                            time,
                            "" + binding.etCostPerHour.text.toString(),
                            binding.etTotalSpace.text.toString(),
                            url,
                        )

                        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_PARKING_INFO)
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