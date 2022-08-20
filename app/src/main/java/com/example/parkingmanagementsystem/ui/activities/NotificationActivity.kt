package com.example.parkingmanagementsystem.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.NotificationInfo
import com.example.parkingmanagementsystem.databinding.ActivityNotificationBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationActivity : AppBaseActivity() {
    private val TAG: String = "NotificationActivity"
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notification_List: ArrayList<NotificationInfo>

    //    private lateinit var notificationListAdapter: NotificationListAdapter
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar(binding.toolbarLayout.toolbar)
        title = getString(R.string.notification)

        notification_List = ArrayList<NotificationInfo>()

//        notificationListAdapter = NotificationListAdapter(notification_List, this)
//        binding.rvNotification.adapter = notificationListAdapter

        val notificationListLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotification.layoutManager = notificationListLayoutManager

        loadNotication()
    }

    private fun loadNotication() {
        db.collection(Constants.FirebaseKeys.KEY_NOTIFICATION_INFO)
            .get().addOnSuccessListener { snapshot ->
                Log.d(TAG, "load Notification: ${snapshot.size()}")
                for (snapshot1 in snapshot) {
                    val notification_item = snapshot1.toObject(NotificationInfo::class.java)
                    Log.d(TAG, "loadNotication: $notification_item")


                        notification_List.add(notification_item)



                }
                notification_List.reverse()
//                notificationListAdapter.notifyDataSetChanged()
                binding.rvNotification.visibility = View.VISIBLE
            }.addOnFailureListener {
                Log.d(TAG, "addOnFailureListener: " + it.message)
                toast("" + it.message)
            }
    }
}