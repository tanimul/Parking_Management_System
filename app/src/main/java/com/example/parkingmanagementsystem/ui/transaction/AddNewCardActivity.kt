package com.example.parkingmanagementsystem.ui.transaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.CardModel
import com.example.parkingmanagementsystem.databinding.ActivityAddNewCardBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.utils.Constants.REQUEST_CODE_ADD_CARD
import com.example.parkingmanagementsystem.utils.extentions.toast
import java.io.Serializable


class AddNewCardActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "AddNewCardActivity"
    }

    private lateinit var binding: ActivityAddNewCardBinding
    private var name: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Spinner
        itemTypes()
        binding.ibDropdownType.setOnClickListener {
            binding.spinnerType.performClick()
        }
        binding.btnAddCard.setOnClickListener {
            if (binding.radio1.isChecked) {
                addCard()
            } else {
                addMobile()
            }
        }
            binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->

                when (i) {
                    binding.radio1.id -> {
                        binding.cardLayout.visibility = View.VISIBLE
                        binding.mobileBankingLayout.visibility = View.GONE
                    }
                    binding.radio2.id -> {
                        binding.cardLayout.visibility = View.GONE
                        binding.mobileBankingLayout.visibility = View.VISIBLE
                    }
                }
            }

    }

    private fun addMobile() {
        if (binding.etNumber.text.toString().isEmpty() && name
                .isEmpty()
        ) {
            toast("Please Fill all the information's")
        } else {

            val cardModel = CardModel(
                cardName = name,
                cardNumber = binding.etNumber.text.toString(),
                cvv = "",
            )
            val resultIntent = Intent()
            setResult(
                REQUEST_CODE_ADD_CARD,
                resultIntent.putExtra("cardModel", cardModel as Serializable)
            )
            Log.d(TAG, "addMobile: ")

        }

        finish()
    }
    private fun addCard() {
        if (binding.etCardNumber.text.toString().isEmpty() && binding.etCardName.text.toString()
                .isEmpty() && binding.etCvv.text.toString()
                .isEmpty()
        ) {
            toast("Please Fill all the information's")
        } else {

            val cardModel = CardModel(
                cardName = binding.etCardName.text.toString(),
                cardNumber = binding.etCardNumber.text.toString(),
                cvv = binding.etCvv.text.toString(),
            )
            val resultIntent = Intent()
            setResult(
                REQUEST_CODE_ADD_CARD,
                resultIntent.putExtra("cardModel", cardModel as Serializable)
            )
            Log.d(TAG, "addCard: ")

        }

        finish()
    }

    private fun itemTypes() {

        val slot_types = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(com.example.parkingmanagementsystem.R.array.type_mobile)
        )
        slot_types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = slot_types
        name=binding.spinnerType.selectedItem.toString()
    }
}