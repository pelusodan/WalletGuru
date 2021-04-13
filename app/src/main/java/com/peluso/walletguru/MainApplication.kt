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
        // for testing, we add some account balance updates
        testAddAccount()
    }

    companion object {
        var database : LocalDatabase? = null
        fun getDbInstance() : LocalDatabase? {
            return database
        }
    }

    private fun testAddAccount() {
        thread {
            db.accountsDao().updateBalance(
                AccountDto(
                    accountBalance = 3f,
                    accountName = "investment",
                    percentChange = 0f,
                    date = System.currentTimeMillis()
                ),
                AccountDto(
                    accountBalance = 4f,
                    accountName = "investment",
                    // percent change equation
                    percentChange = 100f * ((103f - 100f) / 100f),
                    date = System.currentTimeMillis() + 2000L
                ),
                AccountDto(
                    accountBalance = 1f,
                    accountName = "crypto",
                    // percent change equation
                    percentChange = 1f,
                    date = System.currentTimeMillis()
                ),
                AccountDto(
                    accountBalance = 2f,
                    accountName = "crypto",
                    // percent change equation
                    percentChange = 1f,
                    date = System.currentTimeMillis() + 1000L
                ),
                AccountDto(
                    accountBalance = 5f,
                    accountName = "mortgage",
                    // percent change equation
                    percentChange = 19f,
                    date = System.currentTimeMillis() + 1000L
                ),
                AccountDto(
                    accountBalance = 30f,
                    accountName = "saving",
                    // percent change equation
                    percentChange = 10f,
                    date = System.currentTimeMillis() + 1000L
                ),
                AccountDto(
                    accountBalance = 30f,
                    accountName = "crypto",
                    // percent change equation
                    percentChange = 99f,
                    date = System.currentTimeMillis() + 8000L
                ),
                AccountDto(
                    accountBalance = 4000f,
                    accountName = "real_estate",
                    // percent change equation
                    percentChange = 0f,
                    date = System.currentTimeMillis() + 8000L
                )
            )
        }
    }

    fun getDb(): LocalDatabase = db
}
