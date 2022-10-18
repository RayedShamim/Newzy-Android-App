package com.example.newzy.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CountryNewzy(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String,
    val code: String
    )
