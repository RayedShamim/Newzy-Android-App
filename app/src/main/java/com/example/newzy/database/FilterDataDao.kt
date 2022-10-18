package com.example.newzy.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface FilterDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountry(country: CountryNewzy)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilterData(filterData: FilterData)

    @Update
    suspend fun updateFilterData(filterData: FilterData)

    @Update
    suspend fun updateCountry(country: CountryNewzy)

    @Query(value = "SELECT * FROM filter_data ORDER BY id LIMIT 1")
    fun getLatestFilterData(): Flow<FilterData>

    @Query(value = "SELECT * FROM countrynewzy")
    fun getCountry(): Flow<CountryNewzy>

}