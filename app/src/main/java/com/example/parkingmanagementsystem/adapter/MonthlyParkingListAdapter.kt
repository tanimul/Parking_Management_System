package com.example.parkingmanagementsystem.adapter


import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingInfo
import com.example.parkingmanagementsystem.data.model.response.NotificationInfo
import com.example.parkingmanagementsystem.databinding.LayoutMonthlyParkingBinding
import com.example.parkingmanagementsystem.databinding.LayoutNotificationBinding
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.text.SimpleDateFormat

class MonthlyParkingListAdapter(
    private val mMonthlyParkingItems: List<MonthlyParkingInfo>,
    private val lat: Double,
    private val lon: Double,
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
                tvAddress.text = placeAddress
                tvAmount.text = "$ultimateCost BDT"
                ivPlace.loadImageFromUrl(placeUrl)
                tvDistance.text =
                    String.format(
                        "%.2f",
                        tvDistance(
                            LatLng(lat, lon),
                            LatLng(placeLatitude, placeLongitude)
                        ) / 1000
                    ) + " km"
            }
        }


    }

    private fun tvDistance(latLng: LatLng, latLng1: LatLng): Double {
        val distance = SphericalUtil.computeDistanceBetween(latLng, latLng1)
        return distance
    }

    override fun getItemCount(): Int {
        return mMonthlyParkingItems.size
    }
}