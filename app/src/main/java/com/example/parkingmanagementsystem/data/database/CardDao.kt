package com.example.parkingmanagementsystem.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.parkingmanagementsystem.data.model.response.CardModel

@Dao
interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSingleCard(cardModel: CardModel) //Add single card

    @Delete
    suspend fun deleteSingleCard(cardModel: CardModel) //delete single card

    @Query("SELECT * FROM card ORDER BY id DESC")
    fun showAllCards(): LiveData<List<CardModel>> //showing all cards


}