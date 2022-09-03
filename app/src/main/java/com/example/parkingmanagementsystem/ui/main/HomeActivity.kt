package com.example.parkingmanagementsystem.ui.main


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.ParkingInfo
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityHomeBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.activities.MonthlyParkingActivity
import com.example.parkingmanagementsystem.ui.activities.NotificationActivity
import com.example.parkingmanagementsystem.ui.activities.UsePromoCodeActivity
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_PARKING_INFO
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_USERS_COLLECTION
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.security.AccessController.getContext
import java.util.*

class HomeActivity : AppBaseActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener,
    NavigationBarView.OnItemSelectedListener {
    companion object {
        private const val TAG: String = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHeaderLayoutBinding: NavHeaderLayoutBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 101

    lateinit var cur_location: Location
    private lateinit var mMap: GoogleMap

    private var marker: Marker? = null
    private var marker_places: Marker? = null
    val db = Firebase.firestore
    private var addressName: String = "Current Location"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarWithoutBackButton(binding.layoutToolbar.toolbar)
        title = getString(R.string.home)

        //init
        mAuth = FirebaseAuth.getInstance()

        //PLaces API Declare
        initPlacesAPI()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.layoutDrawer,
            binding.layoutToolbar.toolbar,
            R.string.openNavDrawer, R.string.closeNavDrawer
        )
        binding.layoutDrawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navHeaderLayoutBinding =
            NavHeaderLayoutBinding.bind(binding.navBar.getHeaderView(0))

        mAuth.currentUser?.let { setHeaderInformation(it.uid) }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //current location face
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this@HomeActivity)
        fetchLastLocation()

        getParkingSpace()


        binding.navBar.setNavigationItemSelectedListener(this)
    }

    private fun getParkingSpace() {
        marker_places?.remove()
        db.collection(KEY_PARKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "getParkingSpace: ${snapshot.size()}")
                for (parking_space in snapshot) {
                    val parkingInfo = parking_space.toObject<ParkingInfo>()
                    setParkingInfo(parkingInfo)
                }
            }
    }
    private fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun setParkingInfo(parkingInfo: ParkingInfo) {

        val latLng=LatLng(parkingInfo.placeLatitude,parkingInfo.placeLongitude)
        marker_places = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(getBitmapDescriptorFromVector(this, R.drawable.ic_parking))
                .title("" + parkingInfo.placeName)
        )!!
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

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.top_menu, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return true
    }

    private fun setHeaderInformation(uid: String) {
        db.collection(KEY_USERS_COLLECTION)
            .document(uid).get().addOnSuccessListener { snapshot ->
                val user_info = snapshot.toObject<User>()
                Log.d(TAG, "setHeaderInformation: $user_info")
                navHeaderLayoutBinding.tvHeaderName.text = user_info?.name
                navHeaderLayoutBinding.tvHeaderPhoneNumber.text = user_info?.phoneNumber
                user_info?.let { navHeaderLayoutBinding.ivHeaderUser.loadImageFromUrl(it.imageUrl) }

            }

    }

    private fun logOut() {
        AlertDialog.Builder(this@HomeActivity)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to Logged Out?")
            .setPositiveButton(
                "OK"
            ) { _, _ ->

                mAuth.signOut()
                finish()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialog, _ -> dialog.dismiss() }
            .show()

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
            addressName = addressList[0].getAddressLine(0)

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
            .build(this@HomeActivity)

        activityPlacesResultLauncher.launch(intent)

    }

    var activityPlacesResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val place: Place = Autocomplete.getPlaceFromIntent(data)
                Log.d(TAG, "place address: " + place.address)
                addressName = place.name
                val latLng = LatLng(place.latLng.latitude, place.latLng.longitude)
                moveCamera(latLng)
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_notification -> {
                launchActivity<NotificationActivity>()
            }
            R.id.menu_search -> {
                searchPlaces()
            }

        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            //Navigation Drawer Select

            R.id.nav_profile -> {
                launchActivity<ProfileActivity>()
            }

            R.id.nav_use_promo_code -> {
                launchActivity<UsePromoCodeActivity>()
            }
            R.id.nav_monthly_parking -> {
                launchActivity<MonthlyParkingActivity>()
            }
            R.id.nav_logout -> {
                logOut()
            }

        }
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }


}