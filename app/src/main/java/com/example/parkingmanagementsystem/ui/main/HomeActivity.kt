package com.example.parkingmanagementsystem.ui.main


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityHomeBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Variables.user
import com.example.parkingmanagementsystem.utils.extentions.getSharedPrefInstance
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHeaderLayoutBinding: NavHeaderLayoutBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
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

        Log.d(TAG, "onCreate: $user")
        setHeaderInformation(user)


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

    private fun setHeaderInformation(user: User) {
        navHeaderLayoutBinding.tvHeaderName.text = user.name
        navHeaderLayoutBinding.tvHeaderPhoneNumber.text = user.phoneNumber
        navHeaderLayoutBinding.ivHeaderUser.loadImageFromUrl(user.imageUrl)

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

}