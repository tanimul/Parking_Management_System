package com.example.parkingmanagementsystem.ui.parking_add

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivityAddressPickupBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddressPickupActivity : AppCompatActivity(),OnMapReadyCallback {

    companion object {
        private const val TAG: String = "AddressPickupActivity"
    }

    private lateinit var binding: ActivityAddressPickupBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 101

    lateinit var cur_location: Location
    private lateinit var mMap: GoogleMap

    private var marker: Marker? = null
    val db = Firebase.firestore
    private var addressName: String = "Current Location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddressPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onMapReady(p0: GoogleMap) {

    }
}