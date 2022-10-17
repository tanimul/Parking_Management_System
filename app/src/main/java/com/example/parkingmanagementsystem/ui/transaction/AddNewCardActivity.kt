package com.example.parkingmanagementsystem.ui.transaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddCard.setOnClickListener {
            addCard()
        }
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
}