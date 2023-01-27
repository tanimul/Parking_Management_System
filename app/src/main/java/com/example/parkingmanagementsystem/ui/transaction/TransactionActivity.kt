package com.example.parkingmanagementsystem.ui.transaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.adapter.CardListAdapter
import com.example.parkingmanagementsystem.adapter.TransactionListAdapter
import com.example.parkingmanagementsystem.data.model.response.PaymentInfo
import com.example.parkingmanagementsystem.databinding.ActivityAddNewCardBinding
import com.example.parkingmanagementsystem.databinding.ActivityTransactionBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TransactionActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "TransactionActivity"
    }

    private lateinit var binding: ActivityTransactionBinding

    private lateinit var transactionList: ArrayList<PaymentInfo>

    private lateinit var transactionListAdapter: TransactionListAdapter
    val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbarLayout.toolbar)
        title = getString(R.string.transaction)

        //init
        mAuth = FirebaseAuth.getInstance()
        transactionList = ArrayList<PaymentInfo>()

        transactionListAdapter = TransactionListAdapter(transactionList)
        binding.rvTransaction.adapter = transactionListAdapter

        val donationListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTransaction.layoutManager = donationListLayoutManager

        loadTransaction()
    }

    private fun loadTransaction() {
        Log.d(TAG, "User Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID)}")
        Log.d(TAG, "PARKING_OWNER Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID)}")
        Log.d(TAG, "MANAGEMENT Id: ${SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID)}")
        db.collection(Constants.FirebaseKeys.KEY_TRANSACTION_INFO)
            .orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "loadTransaction: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val transactionItem = snapshot1.toObject(PaymentInfo::class.java)
                    Log.d(TAG, "loadTransaction: $transactionItem")
                    if (SharedPrefUtils().getStringValue(Constants.SharedPref.PARKING_OWNER_ID) == transactionItem.receiverId) {
                        transactionList.add(transactionItem)
                    }
                    if(SharedPrefUtils().getStringValue(Constants.SharedPref.MANAGEMENT_ID) != ""){
                        transactionList.add(transactionItem)
                    }
                    if(SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID) == transactionItem.uid){
                        transactionList.add(transactionItem)
                    }

                }

                transactionListAdapter.notifyDataSetChanged()
                binding.rvTransaction.visibility = View.VISIBLE

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