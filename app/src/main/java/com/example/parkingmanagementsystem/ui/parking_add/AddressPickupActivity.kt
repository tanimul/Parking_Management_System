package com.example.parkingmanagementsystem.ui.parking_add

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.databinding.ActivityAddressPickupBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.main.HomeManagementActivity
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddressPickupActivity : AppCompatActivity(), OnMapReadyCallback {

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
    private var lat: Double = 0.0
    private var long: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //PLaces API Declare
        initPlacesAPI()

        //current location face
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment_3) as SupportMapFragment?
        mapFragment?.getMapAsync(this@AddressPickupActivity)
        fetchLastLocation()

        binding.tvAddress.setOnClickListener {
            searchPlaces()
        }

        binding.btnOk.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("addressName", addressName)
            resultIntent.putExtra("lat", lat)
            resultIntent.putExtra("long", long)
            Log.d(TAG, "onCreate: $lat - $long")
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }

    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                // Got last known location. In some rare situations this can be null.
                Log.d(TAG, "onCreate: $it")
                if (it != null) {
                    cur_location = it
                    moveCamera(cur_location)
                }
            }
    }

    private fun initPlacesAPI() {
        Places.initialize(applicationContext, "" + resources.getString(R.string.map_Api_key));
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.setOnMapClickListener {
            val latLng = LatLng(it.latitude, it.longitude)
            moveCamera(latLng)
            val geocoder =
                Geocoder(this, Locale.getDefault())
            val addressList =
                geocoder.getFromLocation(it.latitude, it.longitude, 1)
            addressName=addressList[0].getAddressLine(0)

            binding.tvAddress.text=addressName

            lat = it.latitude
            long = it.longitude

        }
    }

    private fun moveCamera(latLng: LatLng) {
        marker?.remove()
        marker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("" + addressName)
        )!!
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun moveCamera(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        marker?.remove()
        marker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("" + addressName)
        )!!

//        mMap.addCircle(
//            CircleOptions()
//                .center(currentLocation)
//                .radius(10000.0)
//                .strokeColor(Color.RED)
//                .fillColor(Color.argb(70, 150, 50, 50))
//        )
    }

    private fun searchPlaces() {
        val fields: List<Place.Field> = Arrays.asList(
            Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
            Place.Field.TYPES
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        )
            .setCountry("bd")
            .setHint("Search for Places")
            .build(this@AddressPickupActivity)

        activityPlacesResultLauncher.launch(intent)

    }

    var activityPlacesResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val place: Place = Autocomplete.getPlaceFromIntent(data)
                Log.d(TAG, "place address: " + place.address)
                addressName = place.address
                lat = place.latLng.latitude
                long = place.latLng.longitude
                binding.tvAddress.text = addressName
                val latLng = LatLng(place.latLng.latitude, place.latLng.longitude)
                moveCamera(latLng)
            }
        }


}