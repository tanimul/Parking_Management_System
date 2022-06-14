package com.example.parkingmanagementsystem.ui.main


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityHomeBinding
import com.example.parkingmanagementsystem.databinding.NavHeaderLayoutBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Variables.user
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl

class HomeActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHeaderLayoutBinding: NavHeaderLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarWithoutBackButton(binding.layoutToolbar.toolbar)
        title = getString(R.string.home)

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

    private fun setHeaderInformation(user: User) {
        navHeaderLayoutBinding.tvHeaderName.text = user.name
        navHeaderLayoutBinding.tvHeaderPhoneNumber.text = user.phoneNumber
        navHeaderLayoutBinding.ivHeaderUser.loadImageFromUrl(user.imageUrl)

    }
}