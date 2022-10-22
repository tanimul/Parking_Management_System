package com.example.parkingmanagementsystem.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.listener.MonthlyParkingSlotOnClickListener
import com.example.parkingmanagementsystem.databinding.LayoutSlotBinding
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class MonthlyParkingSlotAdapter(
    private val mMonthlyParkingTime: String,
    private val mMonthlyParkingCost: String,
    private val mMonthlyParkingSlot: String,
    private val monthlyParkingSlotOnClickListener: MonthlyParkingSlotOnClickListener
) :
    RecyclerView.Adapter<MonthlyParkingSlotAdapter.FoodListViewHolder>() {
    private val TAG = "MonthlyParkingListAdapter"

    inner class FoodListViewHolder(val binding: LayoutSlotBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodListViewHolder {
        return FoodListViewHolder(
            LayoutSlotBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FoodListViewHolder, position: Int) {
        with(holder.binding) {
         tvTime.text=mMonthlyParkingTime
         tvPrice.text=mMonthlyParkingCost +" BDT"
        }

        holder.binding.checkboxMeat.setOnClickListener {
            monthlyParkingSlotOnClickListener.onItemClick( position)
        }


    }


    override fun getItemCount(): Int {
        return mMonthlyParkingSlot.toInt()
    }
}