package com.example.parkingmanagementsystem.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.NotificationInfo
import com.example.parkingmanagementsystem.databinding.LayoutNotificationBinding
import java.text.SimpleDateFormat

class NotificationListAdapter(
    private val mNotificationItems: List<NotificationInfo>,
) :
    RecyclerView.Adapter<NotificationListAdapter.FoodListViewHolder>() {
    private val TAG = "NotificationListAdapter"
    private var formater = SimpleDateFormat("yyyy.MM.dd HH:mm aa")

    inner class FoodListViewHolder(val binding: LayoutNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        return FoodListViewHolder(
            LayoutNotificationBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {
        with(holder.binding) {
            with(mNotificationItems[position]) {
             tvPurpose.text=purpose
             tvTime.text=formater.format(time)

            }
        }



    }

    override fun getItemCount(): Int {
        return mNotificationItems.size
    }
}