package com.peluso.walletguru

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.peluso.walletguru.database.LocalDatabase
import com.peluso.walletguru.model.AccountDto
import kotlin.concurrent.thread

class MainApplication : Application() {


    private lateinit var db: LocalDatabase
    private val TAG: String = this.javaClass.name

    override fun onCreate() {
        Log.wtf(TAG, "Application created")
        db = Room.databaseBuilder(
            applicationContext,
            LocalDatabase::class.java,
            "local-database"
        )
            .fallbackToDestructiveMigration()
            .build()
        super.onCreate()
    }

    fun getDb(): LocalDatabase = db
}
