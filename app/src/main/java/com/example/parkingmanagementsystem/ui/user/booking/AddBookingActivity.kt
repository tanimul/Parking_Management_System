package com.example.parkingmanagementsystem.ui.user.booking

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.adapter.MonthlyParkingSlotAdapter
import com.example.parkingmanagementsystem.data.listener.MonthlyParkingSlotOnClickListener
import com.example.parkingmanagementsystem.data.model.response.BookingInfo
import com.example.parkingmanagementsystem.data.model.response.ParkingInfo
import com.example.parkingmanagementsystem.databinding.ActivityAddBookingBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.transaction.PaymentAddActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class AddBookingActivity : AppBaseActivity(), MonthlyParkingSlotOnClickListener {
    companion object {
        private const val TAG: String = "AddBookingActivity"
    }

    private lateinit var binding: ActivityAddBookingBinding

    lateinit var itemResponse: ParkingInfo
    private lateinit var monthlyParkingSlotAdapter: MonthlyParkingSlotAdapter
    private lateinit var slot: ArrayList<Int>

    private var date: String = ""
    private var time: String = ""
    val db = Firebase.firestore
    var availableSpace = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slot = ArrayList<Int>()

        //Spinner
        itemTypes()

        binding.ibDropdownSlot.setOnClickListener {
            binding.spinnerSlot.performClick()
        }
        binding.tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.tvDate.text = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                date = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                availableSpace = itemResponse.totalParkingSpace.toInt()
                getAvailableSlot(itemResponse.totalParkingSpace)
            }, year, month, day)

            dpd.show()

        }
        intent.getSerializableExtra("parkingInfo")?.let {
            val parkingInfo = it as ParkingInfo
            itemResponse = ParkingInfo(
                uploaderId = parkingInfo.uploaderId,
                key = parkingInfo.key,
                placeName = parkingInfo.placeName,
                placeAddress = parkingInfo.placeAddress,
                placeLatitude = parkingInfo.placeLatitude,
                placeLongitude = parkingInfo.placeLongitude,
                priority = parkingInfo.priority,
                ultimateCostPerHour = parkingInfo.ultimateCostPerHour,
                totalParkingSpace = parkingInfo.totalParkingSpace,
                placeUrl = parkingInfo.placeUrl,
                addedAt = parkingInfo.addedAt,
                updatedAt = parkingInfo.updatedAt
            )
            setValue()
        }
        availableSpace = itemResponse.totalParkingSpace.toInt()

        val monthlyParkingSlotListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBooking.layoutManager = monthlyParkingSlotListLayoutManager

        binding.spinnerSlot.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                time = binding.spinnerSlot.selectedItem.toString()
                availableSpace = itemResponse.totalParkingSpace.toInt()
                getAvailableSlot(itemResponse.totalParkingSpace)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        getAvailableSlot(itemResponse.totalParkingSpace)

        binding.btnBooking.setOnClickListener {
            if (slot.isNotEmpty()) {
                val bookingInfo = BookingInfo(
                    key = System.currentTimeMillis().toString(),
                    bookingId = System.currentTimeMillis().toString(),
                    bookingDate = binding.tvDate.text.toString(),
                    bookingTime = binding.spinnerSlot.selectedItem.toString(),
                    userId = SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID),
                    parkingId = itemResponse.key,
                    totalParkingSpace = slot.size.toString(),
                    ultimateCost = itemResponse.ultimateCostPerHour,
                    placeName = itemResponse.placeName,
                    placeUrl = itemResponse.placeUrl,
                    addedAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()

                )
                startActivity(
                    Intent(this, PaymentAddActivity::class.java).putExtra(
                        "bookingInfo", bookingInfo
                    ).putExtra("ultimateCost", itemResponse.ultimateCostPerHour)
                        .putExtra("totalSpace", itemResponse.totalParkingSpace)
                        .putExtra("uploaderId", itemResponse.uploaderId)
                )
            } else {
                toast("Please add minimum one slot")
            }
        }

    }

    private fun getAvailableSlot(totalParkingSpace: String) {
        db.collection(Constants.FirebaseKeys.KEY_BOOKING_INFO).get()
            .addOnSuccessListener { snapshot ->
                Log.d(TAG, "load booking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val booking_item = snapshot1.toObject(BookingInfo::class.java)

                    Log.d(TAG, "load Parking: ${itemResponse.key} - ${booking_item.parkingId}")
                    Log.d(TAG, "load Parking: $date - ${booking_item.bookingDate}")
                    Log.d(
                        TAG,
                        "load Parking: ${time} - ${booking_item.bookingTime}"
                    )

                    if (booking_item.parkingId == itemResponse.key && booking_item.bookingDate == date && booking_item.bookingTime == time) {
                        availableSpace = totalParkingSpace.toInt() - 1
                        Log.d(TAG, "getAvailableSlot: $availableSpace")
                    }

                }

                monthlyParkingSlotAdapter = MonthlyParkingSlotAdapter(
                    itemResponse.priority, itemResponse.ultimateCostPerHour, availableSpace, this
                )
                binding.rvBooking.adapter = monthlyParkingSlotAdapter
                monthlyParkingSlotAdapter.notifyDataSetChanged()

            }.addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: " + it.message)
                toast("" + it.message)
            }
    }

    private fun setValue() {
        Log.d(TAG, "setValue: $itemResponse")
        binding.ivPlace.loadImageFromUrl(itemResponse.placeUrl)
        binding.tvAddress.text = itemResponse.placeAddress
        binding.tvDescription.text = itemResponse.placeName

        val c = Calendar.getInstance()
        date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(
            Calendar.YEAR
        )
        binding.tvDate.text = date
        availableSpace = itemResponse.totalParkingSpace.toInt()
    }

    override fun onItemClick(position: Int) {
        if (slot.contains(position)) {
            slot.remove(position)
        } else {
            slot.add(position)
        }
    }

    private fun itemTypes() {

        val slot_types = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            resources.getStringArray(com.example.parkingmanagementsystem.R.array.slot)
        )
        slot_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSlot.adapter = slot_types
        time=binding.spinnerSlot.selectedItem.toString()
    }
}