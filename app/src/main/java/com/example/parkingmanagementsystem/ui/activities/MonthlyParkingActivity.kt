package com.example.parkingmanagementsystem.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.adapter.MonthlyParkingListAdapter
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo
import com.example.parkingmanagementsystem.databinding.ActivityMonthlyParkingBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MonthlyParkingActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "MonthlyParkingActivity"
    }

    private lateinit var binding: ActivityMonthlyParkingBinding

    private lateinit var monthlyParking_List: ArrayList<MonthlyParkingInfo>

    private lateinit var monthlyParkingListAdapter: MonthlyParkingListAdapter
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyParkingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbarLayout.toolbar)
        title = getString(R.string.monthly_parking)

        monthlyParking_List = ArrayList<MonthlyParkingInfo>()

        val bundle = intent.extras
        val lat = bundle!!.getDouble("cur_latitude")
        val lon = bundle!!.getDouble("cur_longitude")
        Log.d(TAG, "onCreate: $lat")
        Log.d(TAG, "onCreate: $lon")

        loadMonthlyParkingList()


        monthlyParkingListAdapter = MonthlyParkingListAdapter(monthlyParking_List,lat,lon)
        binding.rvMonthlyParking.adapter = monthlyParkingListAdapter

        val monthlyParkingListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvMonthlyParking.layoutManager = monthlyParkingListLayoutManager




    }


    private fun loadMonthlyParkingList() {
        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_PARKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "load Monthly Parking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val monthlyParking_item = snapshot1.toObject(MonthlyParkingInfo::class.java)
                    Log.d(TAG, "load Monthly Parking: $monthlyParking_item")


                    monthlyParking_List.add(monthlyParking_item)


                }
                monthlyParking_List.reverse()
                monthlyParkingListAdapter.notifyDataSetChanged()
                binding.rvMonthlyParking.visibility = View.VISIBLE

                binding.shimmerViewContainer.stopShimmerAnimation()
                binding.shimmerViewContainer.visibility = View.GONE
                if (snapshot.isEmpty) {
                    binding.emptyLayout.root.visibility = View.VISIBLE
                }
            }.addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: " + it.message)
                toast("" + it.message)
            }
    }

    override fun onStart() {
        super.onStart()
        binding.shimmerViewContainer.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
    }
}