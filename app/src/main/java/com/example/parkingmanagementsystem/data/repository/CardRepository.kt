package com.example.parkingmanagementsystem.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.parkingmanagementsystem.data.database.CardDao
import com.example.parkingmanagementsystem.data.model.response.CardModel

class CardRepository(private val cardDao: CardDao) {
    private val TAG= "CardRepository"
    var showAllCards: LiveData<List<CardModel>> = cardDao.showAllCards()

    suspend fun addSingleCard(cardModel: CardModel) {
        cardDao.addSingleCard(cardModel)
        Log.d(TAG, "addSingleCard: ")
    }

    suspend fun deleteSingleCard(cardModel: CardModel) {
        cardDao.deleteSingleCard(cardModel)
        Log.d(TAG, "deleteSingleNote: ")
    }
}