package com.example.parkingmanagementsystem.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo
import com.example.parkingmanagementsystem.data.model.response.NotificationInfo
import com.example.parkingmanagementsystem.databinding.LayoutMonthlyParkingBinding
import com.example.parkingmanagementsystem.databinding.LayoutNotificationBinding
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import java.text.SimpleDateFormat

class MonthlyParkingListAdapter(
    private val mMonthlyParkingItems: List<MonthlyParkingInfo>,
) :
    RecyclerView.Adapter<MonthlyParkingListAdapter.FoodListViewHolder>() {
    private val TAG = "MonthlyParkingListAdapter"

    inner class FoodListViewHolder(val binding: LayoutMonthlyParkingBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        return FoodListViewHolder(
            LayoutMonthlyParkingBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {
        with(holder.binding) {
            with(mMonthlyParkingItems[position]) {

                tvAddress.text=placeAddress
                tvAmount.text=ultimateCost
                ivPlace.loadImageFromUrl(placeUrl)
            }
        }



    }

    override fun getItemCount(): Int {
        return mMonthlyParkingItems.size
    }
}