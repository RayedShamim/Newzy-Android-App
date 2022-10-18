package com.example.newzy

import android.app.Application
import com.example.newzy.database.NewzyDatabase

class NewzyApplication: Application() {
    val database: NewzyDatabase by lazy { NewzyDatabase.getDatabase(this) }
}