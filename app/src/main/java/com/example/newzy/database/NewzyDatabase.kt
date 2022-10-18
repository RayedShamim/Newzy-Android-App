package com.example.newzy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CountryNewzy::class,FilterData::class], version = 1, exportSchema = false)
abstract class NewzyDatabase: RoomDatabase() {

    abstract fun filterDataDao(): FilterDataDao

    companion object {
        @Volatile
        private var INSTANCE : NewzyDatabase? = null

        fun getDatabase(context: Context): NewzyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewzyDatabase::class.java,
                    "newzy_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}