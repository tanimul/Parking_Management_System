package com.example.parkingmanagementsystem.ui.admin.booking

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "BookingActivity"
    }

    private lateinit var binding: ActivityBookingBinding

    private lateinit var fillterdBookingList: ArrayList<BookingInfo>
    private lateinit var bookingList: ArrayList<BookingInfo>
    private lateinit var monthlyBookingList: ArrayList<BookingInfo>

    private lateinit var bookingListAdapter: BookingListAdapter
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    private var formater = SimpleDateFormat("dd/MM/yyy")
    private var formaterMonth = SimpleDateFormat("MMM")

    var map_month: HashMap<String, Int> = hashMapOf("Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3, "May" to 4, "Jun" to 5,"Jul" to 6, "Aug" to 7, "Sep" to 8, "Oct" to 9, "Nov" to 10, "Dec" to 11)

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbarLayout.toolbar)
        title = getString(R.string.bookings)

        //init
        mAuth = FirebaseAuth.getInstance()
        fillterdBookingList = ArrayList<BookingInfo>()
        bookingList = ArrayList<BookingInfo>()
        monthlyBookingList = ArrayList<BookingInfo>()

        bookingListAdapter = BookingListAdapter(fillterdBookingList)
        binding.rvBooking.adapter = bookingListAdapter

        val donationListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvBooking.layoutManager = donationListLayoutManager

        loadBooking()
        loadMonthlyBooking()

        val currentDate = formater.format(Date())
        val currentMonth = formaterMonth.format(Date())

        binding.btnOngoing.setOnClickListener {

            val filteredList1 =
                bookingList.filter {
                    it.bookingDate.startsWith(currentDate)
                }

            val filteredList2 =
                monthlyBookingList.filter {
                    it.bookingDate.startsWith(currentMonth)
                }

            fillterdBookingList.clear()
            fillterdBookingList.addAll(filteredList1)
            fillterdBookingList.addAll(filteredList2)

            binding.rvBooking.adapter = BookingListAdapter(fillterdBookingList)

            binding.btnOngoing.setBackgroundColor(R.color.color_secondary)
            binding.btnUpcoming.setBackgroundColor(Color.BLUE)
            binding.btnPrevious.setBackgroundColor(Color.BLUE)
        }

        binding.btnPrevious.setOnClickListener {

            val filteredList1 =
                bookingList.filter {
                    formater.parse(it.bookingDate) < formater.parse(currentDate)
                }

            val filteredList2 =
                monthlyBookingList.filter {
                    map_month[it.bookingDate]!! < map_month[currentMonth]!!
                }

            fillterdBookingList.clear()
            fillterdBookingList.addAll(filteredList1)
            fillterdBookingList.addAll(filteredList2)

            binding.rvBooking.adapter = BookingListAdapter(fillterdBookingList)

            binding.btnOngoing.setBackgroundColor(Color.BLUE)
            binding.btnUpcoming.setBackgroundColor(Color.BLUE)
            binding.btnPrevious.setBackgroundColor(R.color.color_secondary)

        }

        binding.btnUpcoming.setOnClickListener {
            val filteredList1 =
                bookingList.filter {
                    formater.parse(it.bookingDate) > formater.parse(currentDate)
                }

            val filteredList2 =
                monthlyBookingList.filter {
                    map_month[it.bookingDate]!! > map_month[currentMonth]!!
                }

            fillterdBookingList.clear()
            fillterdBookingList.addAll(filteredList1)
            fillterdBookingList.addAll(filteredList2)


            binding.rvBooking.adapter = BookingListAdapter(fillterdBookingList)

            binding.btnOngoing.setBackgroundColor(Color.BLUE)
            binding.btnUpcoming.setBackgroundColor(R.color.color_secondary)
            binding.btnPrevious.setBackgroundColor(Color.BLUE)
        }
    }

    private fun loadBooking() {
        Log.d(TAG, "User Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID)}")
        Log.d(TAG, "PARKING_OWNER Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)}")
        Log.d(TAG, "MANAGEMENT Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID)}")

        db.collection(Constants.FirebaseKeys.KEY_BOOKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "loadBooking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val bookingItem = snapshot1.toObject(BookingInfo::class.java)
                    Log.d(TAG, "loadBooking: $bookingItem")

                    if (SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID) != "") {
                        bookingList.add(bookingItem)
                    }
                    if(SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID) != ""){
                        bookingList.add(bookingItem)
                    }
                    if(SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID) == bookingItem.userId){
                        bookingList.add(bookingItem)
                    }


                }

                fillterdBookingList.addAll(bookingList)
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

    private fun loadMonthlyBooking() {
        Log.d(TAG, "User Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID)}")
        Log.d(TAG, "PARKING_OWNER Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)}")
        Log.d(TAG, "MANAGEMENT Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID)}")
        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_BOOKING_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "loadBooking: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val bookingItem = snapshot1.toObject(BookingInfo::class.java)
                    Log.d(TAG, "loadBooking: $bookingItem")


                    if (SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID) != "") {
                        monthlyBookingList.add(bookingItem)

                    }
                    if(SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID) != ""){
                        monthlyBookingList.add(bookingItem)

                    }
                    if(SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID) == bookingItem.userId){
                        monthlyBookingList.add(bookingItem)

                    }

                }
                fillterdBookingList.addAll(monthlyBookingList)
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