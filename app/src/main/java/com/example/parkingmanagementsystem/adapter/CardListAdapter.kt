package com.example.parkingmanagementsystem.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.data.database.CardClickListener
import com.example.parkingmanagementsystem.data.model.response.CardModel
import com.example.parkingmanagementsystem.databinding.LayoutSelectionCardBinding

class CardListAdapter(
    private val mCardItems: List<CardModel>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<CardListAdapter.CardListViewHolder>() {
    private val TAG = "CardListAdapter"
    private val selectedView: View? = null
    private var selectedPosition = -1

    inner class CardListViewHolder(val binding: LayoutSelectionCardBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListViewHolder {
        return CardListViewHolder(
            LayoutSelectionCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardListViewHolder, position: Int) {

        if (selectedPosition == position) {
          holder.binding.ivIcon.visibility=View.VISIBLE
        } else {
            holder.binding.ivIcon.visibility=View.GONE
        }

        with(holder.binding) {
            with(mCardItems[position]) {
                tvName.text=cardName
                tvNumber.text=cardNumber

            }
        }
        holder.itemView.setOnClickListener {
            cardClickListener.onClick(position,mCardItems[position])
            selectedPosition = position;
            holder.binding.ivIcon.visibility=View.VISIBLE
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return mCardItems.size
    }
}