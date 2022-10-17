package com.example.parkingmanagementsystem.data.database

import com.example.parkingmanagementsystem.data.model.response.CardModel


interface CardClickListener {
    fun onClick(id: Int, cardModel: CardModel)
}