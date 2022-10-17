package com.example.parkingmanagementsystem.ui.user.monthly_parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.adapter.MonthlyParkingSlotAdapter
import com.example.parkingmanagementsystem.data.listener.MonthlyParkingSlotOnClickListener
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingBookingInfo
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo
import com.example.parkingmanagementsystem.databinding.ActivityAddMonthlyBookingBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.transaction.PaymentAddActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.Variables
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.example.parkingmanagementsystem.utils.extentions.toast
import java.io.Serializable

class AddMonthlyBookingActivity : AppBaseActivity(), MonthlyParkingSlotOnClickListener {
    companion object {
        private const val TAG: String = "AddMonthlyBookingActivity"
    }

    private lateinit var binding: ActivityAddMonthlyBookingBinding

    lateinit var itemResponse: MonthlyParkingInfo
    private lateinit var monthlyParkingSlotAdapter: MonthlyParkingSlotAdapter
    private lateinit var slot: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMonthlyBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getSerializableExtra("monthlyParkingInfo")?.let {
            val monthlyParkingInfo = it as MonthlyParkingInfo
            itemResponse = MonthlyParkingInfo(
                uploaderId = monthlyParkingInfo.uploaderId,
                key = monthlyParkingInfo.key,
                placeName = monthlyParkingInfo.placeName,
                placeAddress = monthlyParkingInfo.placeAddress,
                placeLatitude = monthlyParkingInfo.placeLatitude,
                placeLongitude = monthlyParkingInfo.placeLongitude,
                month = monthlyParkingInfo.month,
                time = monthlyParkingInfo.time,
                ultimateCost = monthlyParkingInfo.ultimateCost,
                totalParkingSpace = monthlyParkingInfo.totalParkingSpace,
                placeUrl = monthlyParkingInfo.placeUrl,
                addedAt = monthlyParkingInfo.addedAt,
                updatedAt = monthlyParkingInfo.updatedAt
            )
            slot = ArrayList<Int>()
            setValue()

        }

        monthlyParkingSlotAdapter = MonthlyParkingSlotAdapter(
            itemResponse.time,
            itemResponse.ultimateCost,
            itemResponse.totalParkingSpace,
            this
        )
        binding.rvBooking.adapter = monthlyParkingSlotAdapter

        val monthlyParkingSlotListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBooking.layoutManager = monthlyParkingSlotListLayoutManager

        binding.btnBooking.setOnClickListener {
            if(slot.isNotEmpty()){
                val monthlyParkingBookingInfo= MonthlyParkingBookingInfo(
                    key=System.currentTimeMillis().toString(),
                    bookingId=System.currentTimeMillis().toString(),
                    userId=SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID),
                    monthlyParkingId=itemResponse.key,
                    totalParkingSpace=slot.size.toString(),
                )
                startActivity(
                    Intent(this, PaymentAddActivity::class.java).putExtra(
                        "monthlyParkingBookingInfo",
                        monthlyParkingBookingInfo
                    ).putExtra("ultimateCost",itemResponse.ultimateCost).putExtra("totalSpace",itemResponse.totalParkingSpace).putExtra("uploaderId",itemResponse.uploaderId)
                )
            }else{
                toast("Please add minimum one slot")
            }
        }

    }

    private fun setValue() {
        binding.ivPlace.loadImageFromUrl(itemResponse.placeUrl)
        binding.tvAddress.text = itemResponse.placeAddress
        binding.tvMonth.text = itemResponse.month
    }

    override fun onItemClick(position: Int) {
        if(slot.contains(position)){
            slot.remove(position)
        }else{
            slot.add(position)
        }
    }


}