package com.example.parkingmanagementsystem.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.model.response.PaymentInfo
import com.example.parkingmanagementsystem.databinding.LayoutTransactionsBinding
import java.text.SimpleDateFormat

class TransactionListAdapter(
    private val transactionItems: List<PaymentInfo>,
) :
    RecyclerView.Adapter<TransactionListAdapter.TransactionListViewHolder>() {
    private val TAG = "TransactionListAdapter"
    private var formater = SimpleDateFormat("yyyy.MM.dd HH:mm aa")

    inner class TransactionListViewHolder(val binding: LayoutTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        return TransactionListViewHolder(
            LayoutTransactionsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        with(holder.binding) {
            with(transactionItems[position]) {
                tvName.text=id
                tvAmount.text= "$amount BDT"
                tvTime.text=formater.format(time)

            }
        }



    }

    override fun getItemCount(): Int {
        return transactionItems.size
    }
}