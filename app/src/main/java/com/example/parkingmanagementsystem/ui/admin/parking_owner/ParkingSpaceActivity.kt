package com.example.parkingmanagementsystem.ui.admin.parking_owner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Space
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.adapter.MonthlyParkingListAdapter
import com.example.parkingmanagementsystem.adapter.ParkingSpaceListAdapter
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo
import com.example.parkingmanagementsystem.data.model.response.ParkingInfo
import com.example.parkingmanagementsystem.data.model.response.SpaceInfo
import com.example.parkingmanagementsystem.databinding.ActivityMonthlyParkingBinding
import com.example.parkingmanagementsystem.databinding.ActivityParkingSpaceBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.user.monthly_parking.MonthlyParkingActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.MANAGEMENT_ID
import com.example.parkingmanagementsystem.utils.Constants.SharedPref.PARKING_OWNER_ID
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ParkingSpaceActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "ParkingSpaceActivity"
    }

    private lateinit var binding: ActivityParkingSpaceBinding

    private lateinit var parking_List: ArrayList<SpaceInfo>

    private lateinit var parkingSpaceListAdapter: ParkingSpaceListAdapter
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParkingSpaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbarLayout.toolbar)
        title = getString(R.string.parking_space)

        parking_List = ArrayList<SpaceInfo>()

        loadMonthlyParkingList()
        loadParkingList()

        parkingSpaceListAdapter = ParkingSpaceListAdapter(parking_List)
        binding.rvParkingSpace.adapter = parkingSpaceListAdapter

        val monthlyParkingListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvParkingSpace.layoutManager = monthlyParkingListLayoutManager
    }

    private fun loadParkingList() {
        Log.d(TAG, "load Parking: ${SharedPrefUtils().getStringValue(PARKING_OWNER_ID)}")
        db.collection(Constants.FirebaseKeys.KEY_PARKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "load Parking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val parking_item = snapshot1.toObject(ParkingInfo::class.java)
                    Log.d(TAG, "load Parking: $parking_item")
                    Log.d(TAG, "load Parking: ${parking_item.uploaderId}")

                    if(SharedPrefUtils().getStringValue(PARKING_OWNER_ID)==parking_item.uploaderId || SharedPrefUtils().getStringValue(
                            MANAGEMENT_ID)==parking_item.uploaderId){
                        val spaceInfo=SpaceInfo(uploaderId = parking_item.uploaderId,key = parking_item.key,placeName = parking_item.placeName,
                            placeAddress = parking_item.placeAddress,ultimateCost = parking_item.ultimateCostPerHour+" /hour",totalParkingSpace = parking_item.totalParkingSpace,
                            placeUrl = parking_item.placeUrl,addedAt = parking_item.uploaderId.toLong(),updatedAt = parking_item.uploaderId.toLong())
                        parking_List.add(spaceInfo)
                    }
                }

                parkingSpaceListAdapter.notifyDataSetChanged()
                binding.rvParkingSpace.visibility = View.VISIBLE

                binding.shimmerViewContainer.stopShimmerAnimation()
                binding.shimmerViewContainer.visibility = View.GONE
            }.addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: " + it.message)
                toast("" + it.message)
            }
    }

    private fun loadMonthlyParkingList() {
        Log.d(TAG, "load Parking: ${SharedPrefUtils().getStringValue(PARKING_OWNER_ID)}")
        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_PARKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "load Monthly Parking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val monthlyParking_item = snapshot1.toObject(MonthlyParkingInfo::class.java)
                    Log.d(TAG, "load Monthly Parking: $monthlyParking_item")
                    Log.d(TAG, "load Parking: ${monthlyParking_item.uploaderId}")
                    if(SharedPrefUtils().getStringValue(PARKING_OWNER_ID)==monthlyParking_item.uploaderId || SharedPrefUtils().getStringValue(
                            MANAGEMENT_ID)==monthlyParking_item.uploaderId){
                        val spaceInfo=SpaceInfo(uploaderId = monthlyParking_item.uploaderId,key = monthlyParking_item.key,placeName = monthlyParking_item.placeName,
                            placeAddress = monthlyParking_item.placeAddress,ultimateCost = monthlyParking_item.ultimateCost,totalParkingSpace = monthlyParking_item.totalParkingSpace,
                            placeUrl = monthlyParking_item.placeUrl,addedAt = monthlyParking_item.uploaderId.toLong(),updatedAt = monthlyParking_item.uploaderId.toLong())
                        parking_List.add(spaceInfo)
                    }
                }

            }.addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: " + it.message)
                toast("" + it.message)
            }
    }
}