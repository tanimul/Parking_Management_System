package com.example.parkingmanagementsystem.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.MonthlyParkingBookingInfo
import com.example.parkingmanagementsystem.data.model.response.PaymentInfo
import com.example.parkingmanagementsystem.databinding.LayoutBookingBinding
import com.example.parkingmanagementsystem.databinding.LayoutTransactionsBinding
import com.example.parkingmanagementsystem.utils.extentions.loadImageFromUrl
import java.text.SimpleDateFormat

class BookingListAdapter(
    private val bookingItems: List<MonthlyParkingBookingInfo>,
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
        with(holder.binding) {
            with(bookingItems[position]) {
                tvPlaceName.text=placeName
                tvAmount.text= "${ultimateCost.toInt()*totalParkingSpace.toInt()} BDT"
                tvDate.text=formater.format(updatedAt)
                ivPlace.loadImageFromUrl(placeUrl)

            }
        }



    }

    override fun getItemCount(): Int {
        return bookingItems.size
    }
}