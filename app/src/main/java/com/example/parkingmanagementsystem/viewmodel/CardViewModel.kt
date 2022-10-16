package com.example.parkingmanagementsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.parkingmanagementsystem.data.database.CardDatabase
import com.example.parkingmanagementsystem.data.repository.CardRepository
import com.example.parkingmanagementsystem.data.model.response.CardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "CardViewModel"
    val showAllCards: LiveData<List<CardModel>>
    private val cardRepository: CardRepository

    init {
        val cardDao = CardDatabase.getDatabase(application).cardDao()
        cardRepository = CardRepository(cardDao)
        showAllCards = cardRepository.showAllCards
    }

    fun addSingleCard(cardModel: CardModel) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.addSingleCard(cardModel)
    }
    fun deleteSingleCard(cardModel: CardModel) = viewModelScope.launch(Dispatchers.IO) {
        cardRepository.deleteSingleCard(cardModel)
    }

}