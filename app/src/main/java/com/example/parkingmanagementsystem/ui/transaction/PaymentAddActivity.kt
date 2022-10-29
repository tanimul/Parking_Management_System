package com.example.parkingmanagementsystem.ui.transaction

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.adapter.CardListAdapter
import com.example.parkingmanagementsystem.data.database.CardClickListener
import com.example.parkingmanagementsystem.data.model.response.BookingInfo
import com.example.parkingmanagementsystem.data.model.response.CardModel
import com.example.parkingmanagementsystem.databinding.ActivityPaymentAddBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants
import com.example.parkingmanagementsystem.utils.Constants.REQUEST_CODE_ADD_CARD
import com.example.parkingmanagementsystem.utils.SharedPrefUtils
import com.example.parkingmanagementsystem.utils.Variables
import com.example.parkingmanagementsystem.utils.extentions.toast
import com.example.parkingmanagementsystem.viewmodel.CardViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class PaymentAddActivity : AppBaseActivity(), CardClickListener {
    companion object {
        private const val TAG: String = "PaymentAddActivity"
    }

    private lateinit var binding: ActivityPaymentAddBinding
    lateinit var itemResponse: BookingInfo

    private val db = Firebase.firestore
    private lateinit var cardViewModel: CardViewModel

    private lateinit var cardList: ArrayList<CardModel>

    private lateinit var cardListAdapter: CardListAdapter
    var cardName = ""
    var cardNumber = ""
    var selection = false
    var totalSpace = 0
    var uploaderId = ""
    var discount = 50
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getSerializableExtra("monthlyParkingBookingInfo")?.let {
            val monthlyParkingBookingInfo = it as BookingInfo
            itemResponse = BookingInfo(
                key = monthlyParkingBookingInfo.key,
                bookingId = monthlyParkingBookingInfo.bookingId,
                bookingDate = monthlyParkingBookingInfo.bookingDate,
                userId = monthlyParkingBookingInfo.userId,
                parkingId = monthlyParkingBookingInfo.parkingId,
                totalParkingSpace = monthlyParkingBookingInfo.totalParkingSpace,
                ultimateCost = monthlyParkingBookingInfo.ultimateCost,
                placeName = monthlyParkingBookingInfo.placeName,
                placeUrl = monthlyParkingBookingInfo.placeUrl,
                addedAt = monthlyParkingBookingInfo.addedAt,
                updatedAt = monthlyParkingBookingInfo.updatedAt
            )
        }

        intent.getSerializableExtra("bookingInfo")?.let {
            val bookingInfo = it as BookingInfo
            itemResponse = BookingInfo(
                key = bookingInfo.key,
                bookingId = bookingInfo.bookingId,
                bookingDate = bookingInfo.bookingDate,
                bookingTime = bookingInfo.bookingTime,
                userId = bookingInfo.userId,
                parkingId = bookingInfo.parkingId,
                totalParkingSpace = bookingInfo.totalParkingSpace,
                ultimateCost = bookingInfo.ultimateCost,
                placeName = bookingInfo.placeName,
                placeUrl = bookingInfo.placeUrl,
                addedAt = bookingInfo.addedAt,
                updatedAt = bookingInfo.updatedAt

            )
        }

        intent.getStringExtra("ultimateCost")?.let {
            if(it.toInt()!=0){
                if(SharedPrefUtils().getStringValue(Constants.SharedPref.PROMO_CODE)!="" || SharedPrefUtils().getStringValue(Constants.SharedPref.PROMO_CODE)!=null){
                    binding.tvAmount.text = ((itemResponse.totalParkingSpace.toInt() * it.toInt()) - discount ) .toString()
                }else{
                    binding.tvAmount.text = ((itemResponse.totalParkingSpace.toInt() * it.toInt()) ) .toString()
                }

            }else{
                binding.tvAmount.text= "0"
            }
        }

        intent.getStringExtra("totalSpace")?.let {
            totalSpace = it.toInt()
        }
        intent.getStringExtra("uploaderId")?.let {
            uploaderId = it
        }

        //card viewModel initialize
        cardViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CardViewModel::class.java]

        cardList = ArrayList<CardModel>()

        cardListAdapter = CardListAdapter(cardList, this)
        binding.rvCard.adapter = cardListAdapter

        val cardListLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvCard.layoutManager = cardListLayoutManager

        loadCard()

        binding.tvAddCard.setOnClickListener {
            val intent = Intent(this, AddNewCardActivity::class.java)
            addCardActResult.launch(intent)
        }
        binding.btnPayment.setOnClickListener {
            if (binding.tvAmount.text.toString().isNotEmpty() && selection) {
//toast(itemResponse.toString())
                  setParking()

            } else {
                toast("Please Select Payment Method and type Amount")
            }
        }
        deleteForSwipe()

    }

    private fun setParking() {
        showProgress(true)
        //get data
        val name = cardName
        val number = cardNumber
        val amount = binding.tvAmount.text.toString().trim()

        var id = System.currentTimeMillis().toString()
        //set user data
        Variables.paymentInfo.id = id
        Variables.paymentInfo.uid = SharedPrefUtils().getStringValue(Constants.SharedPref.USERS_ID)
        Variables.paymentInfo.bookingId = itemResponse.bookingId
        Variables.paymentInfo.receiverId = uploaderId
        Variables.paymentInfo.cardName = name
        Variables.paymentInfo.cardNumber = number
        Variables.paymentInfo.amount = amount


        db.collection(Constants.FirebaseKeys.KEY_TRANSACTION_INFO).document(id)
            .set(Variables.paymentInfo).addOnSuccessListener {
                showProgress(false)
                if(itemResponse.bookingTime==""){
                    setMonthlyBooking()
                }else{
                    setBooking()
                }


            }.addOnFailureListener {
                showProgress(false)
                Log.d(TAG, "${it.localizedMessage!!} ")
                toast(it.localizedMessage!!)
            }


    }
    private fun setBooking() {
        db.collection(Constants.FirebaseKeys.KEY_BOOKING_INFO).document(itemResponse.key)
            .set(itemResponse).addOnSuccessListener {
                showProgress(false)
                Log.d(TAG, "Booking Successfully")
                toast("Booking Successfully")
                finish()
            }.addOnFailureListener {
                showProgress(false)
                Log.d(TAG, "${it.localizedMessage!!} ")
                toast(it.localizedMessage!!)
            }

    }
    private fun setMonthlyBooking() {
        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_BOOKING_INFO).document(itemResponse.key)
            .set(itemResponse).addOnSuccessListener {
                showProgress(false)
                updateMonthlyParking()
            }.addOnFailureListener {
                showProgress(false)
                Log.d(TAG, "${it.localizedMessage!!} ")
                toast(it.localizedMessage!!)
            }

    }

    private fun updateMonthlyParking() {

        val availableSpace = totalSpace - itemResponse.totalParkingSpace.toInt()
        db.collection(Constants.FirebaseKeys.KEY_MONTHLY_PARKING_INFO)
            .document(itemResponse.parkingId)
            .update("totalParkingSpace", availableSpace.toString()).addOnSuccessListener {
                showProgress(false)
                Log.d(TAG, "Booking Successfully")
                toast("Booking Successfully")
                finish()

            }.addOnFailureListener {
                showProgress(false)
                Log.d(TAG, "${it.localizedMessage!!} ")
                toast(it.localizedMessage!!)
            }
    }

    private fun loadCard() {
        cardViewModel.showAllCards.observe(
            this
        ) {
            cardList.clear()
            cardList.addAll(it)
            cardListAdapter.notifyDataSetChanged()

        }
    }

    private val addCardActResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != RESULT_CANCELED) {
            val cardModel: CardModel = it.data?.getSerializableExtra("cardModel") as CardModel
            if (it.resultCode == REQUEST_CODE_ADD_CARD) {
                Log.d(TAG, "ok Add: " + it.data?.getSerializableExtra("cardModel"))
                if (it.data != null) {
                    cardViewModel.addSingleCard(cardModel)
                    toast("Card Saved")
                }

            }
        }

    }

    //Delete by left to right swipe
    private fun deleteForSwipe() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                cardViewModel.deleteSingleCard(cardList[viewHolder.adapterPosition])
                cardListAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                ).addBackgroundColor(
                    ContextCompat.getColor(
                        this@PaymentAddActivity, R.color.colorBackground
                    )
                ).addActionIcon(R.drawable.ic_delete_red).create().decorate()
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.rvCard)
    }

    override fun onClick(id: Int, cardModel: CardModel) {
        cardName = cardModel.cardName
        cardNumber = cardModel.cardNumber
        selection = true
    }

}
