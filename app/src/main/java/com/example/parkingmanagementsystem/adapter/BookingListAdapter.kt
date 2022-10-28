package com.example.parkingmanagementsystem.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.BookingInfo
import com.example.parkingmanagementsystem.data.model.response.PaymentInfo
import com.example.parkingmanagementsystem.databinding.LayoutBookingBinding
import com.example.parkingmanagementsystem.databinding.LayoutTransactionsBinding
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import java.text.SimpleDateFormat

class BookingListAdapter(
    private val bookingItems: List<BookingInfo>,
) :
    RecyclerView.Adapter<BookingListAdapter.TransactionListViewHolder>() {
    private val TAG = "BookingListAdapter"
    private var formater = SimpleDateFormat("yyyy.MM.dd HH:mm aa")

    inner class TransactionListViewHolder(val binding: LayoutBookingBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        return TransactionListViewHolder(
            LayoutBookingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${bookingItems[position]}")
        with(holder.binding) {
            with(bookingItems[position]) {
                tvPlaceName.text=placeName
                tvAmount.text= "${ultimateCost.toInt()*totalParkingSpace.toInt()} BDT"
                //tvDate.text=formater.format(updatedAt)
                tvDate.text=bookingDate
                ivPlace.loadImageFromUrl(placeUrl)

            }
        }

    }

    override fun getItemCount(): Int {
        return bookingItems.size
    }
}