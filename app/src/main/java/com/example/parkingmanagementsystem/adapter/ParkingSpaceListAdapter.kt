package com.example.parkingmanagementsystem.adapter
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.BookingInfo
import com.example.parkingmanagementsystem.data.model.response.PaymentInfo
import com.example.parkingmanagementsystem.data.model.response.SpaceInfo
import com.example.parkingmanagementsystem.databinding.LayoutBookingBinding
import com.example.parkingmanagementsystem.databinding.LayoutParkingSpaceBinding
import com.example.parkingmanagementsystem.databinding.LayoutTransactionsBinding
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import java.text.SimpleDateFormat

class ParkingSpaceListAdapter(
    private val spaceItems: List<SpaceInfo>,
) :
    RecyclerView.Adapter<ParkingSpaceListAdapter.TransactionListViewHolder>() {
    private val TAG = "ParkingSpaceListAdapter"
    private var formater = SimpleDateFormat("yyyy.MM.dd HH:mm aa")

    inner class TransactionListViewHolder(val binding: LayoutParkingSpaceBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        return TransactionListViewHolder(
            LayoutParkingSpaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${spaceItems[position]}")
        with(holder.binding) {
            with(spaceItems[position]) {
                tvName.text=placeName
                tvAmount.text= "$ultimateCost BDT"
                tvAddress.text=placeAddress
                ivPlace.loadImageFromUrl(placeUrl)

            }
        }

    }

    override fun getItemCount(): Int {
        return spaceItems.size
    }
}