package com.example.parkingmanagementsystem.ui.admin.booking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.adapter.BookingListAdapter
import com.example.parkingmanagementsystem.adapter.TransactionListAdapter
import com.example.parkingmanagementsystem.data.model.response.BookingInfo
import com.example.parkingmanagementsystem.data.model.response.PaymentInfo
import com.example.parkingmanagementsystem.databinding.ActivityBookingBinding
import com.example.parkingmanagementsystem.databinding.ActivityTransactionBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.transaction.TransactionActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookingActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "BookingActivity"
    }

    private lateinit var binding: ActivityBookingBinding

    private lateinit var bookingList: ArrayList<BookingInfo>

    private lateinit var bookingListAdapter: BookingListAdapter
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbarLayout.toolbar)
        title = getString(R.string.bookings)

        //init
        mAuth = FirebaseAuth.getInstance()
        bookingList = ArrayList<BookingInfo>()

        bookingListAdapter = BookingListAdapter(bookingList)
        binding.rvBooking.adapter = bookingListAdapter

        val donationListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBooking.layoutManager = donationListLayoutManager

        loadBooking()
    }

    private fun loadBooking() {
        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_BOOKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "loadBooking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val bookingItem = snapshot1.toObject(BookingInfo::class.java)
                    Log.d(TAG, "loadBooking: $bookingItem")

                    bookingList.add(bookingItem)

                }

                bookingListAdapter.notifyDataSetChanged()
                binding.rvBooking.visibility = View.VISIBLE

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