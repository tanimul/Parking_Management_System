package com.example.parkingmanagementsystem.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityHomeManagementBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.activities.NotificationActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.FULL_NAME
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.IMAGE_URL
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.PHONE_NUMBER
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.launchActivity
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeManagementActivity : AppBaseActivity(), OnMapReadyCallback,
    NavigationView.OnNavigationItemSelectedListener,
    NavigationBarView.OnItemSelectedListener {
    companion object {
        private const val TAG: String = "HomeManagementActivity"
    }

    private lateinit var binding: ActivityHomeManagementBinding
    private lateinit var navHeaderLayoutBinding: NavHeaderLayoutBinding

    private lateinit var mAuth: FirebaseAuth
     private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 101

    lateinit var cur_location: Location
    private lateinit var mMap: GoogleMap
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarWithoutBackButton(binding.layoutToolbar.toolbar)
        title = getString(R.string.home)

        //init
        mAuth = FirebaseAuth.getInstance()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.layoutDrawer,
            binding.layoutToolbar.toolbar,
            R.string.openNavDrawer, R.string.closeNavDrawer
        )
        binding.layoutDrawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navHeaderLayoutBinding =
            NavHeaderLayoutBinding.bind(binding.navBar.getHeaderView(0))



        setHeaderInformation()
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                // Got last known location. In some rare situations this can be null.
                if (it != null) {
                    cur_location = it
                }
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map_fragment_2) as? SupportMapFragment
                mapFragment?.getMapAsync(this)
            }

        binding.navBar.setNavigationItemSelectedListener(this)
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

    private fun setHeaderInformation() {

        navHeaderLayoutBinding.tvHeaderName.text = SharedPrefUtils().getStringValue(FULL_NAME, "")
        navHeaderLayoutBinding.tvHeaderPhoneNumber.text =
            SharedPrefUtils().getStringValue(PHONE_NUMBER, "")
        navHeaderLayoutBinding.ivHeaderUser.loadImageFromUrl(
            SharedPrefUtils().getStringValue(
                IMAGE_URL,
                ""
            )
        )

    }

    private fun logOut() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to Logged Out?")
            .setPositiveButton(
                "OK"
            ) { _, _ ->
                SharedPrefUtils().setValue(Constants.SharedPref.IS_LOGGIN, false)
//                mAuth.signOut()
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
        val currentLocation = LatLng(cur_location.latitude, cur_location.longitude)
        Log.d(TAG, "onMapReady: $currentLocation")
        mMap.addMarker(
            MarkerOptions().position(currentLocation).title("Cur_location")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 7f))
        mMap.addCircle(
            CircleOptions()
                .center(currentLocation)
                .radius(10000.0)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(70, 150, 50, 50))
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_notification -> {
                launchActivity<NotificationActivity>()
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
            R.id.nav_logout -> {
                logOut()
            }

        }
        binding.layoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }

}