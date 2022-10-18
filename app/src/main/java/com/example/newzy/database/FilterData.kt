package com.example.newzy.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_data")
data class FilterData(
    @PrimaryKey(autoGenerate = true)
    val id :Int?,
    val sliderCount: Int?,
    val from: Long?,
    val to: Long?,
    val language: String?,
    val sortBy: String?
)