package com.peluso.walletguru

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.peluso.walletguru.database.AccountsDatabase
import com.peluso.walletguru.model.AccountDto
import java.time.Instant
import java.util.*
import kotlin.concurrent.thread

class MainApplication : Application() {


    private lateinit var db: AccountsDatabase
    private val TAG: String = this.javaClass.name

    override fun onCreate() {
        Log.wtf(TAG, "Application created")
        db = Room.databaseBuilder(
            applicationContext,
            AccountsDatabase::class.java,
            "accounts-database"
        )
            .fallbackToDestructiveMigration()
            .build()
        super.onCreate()
        // for testing, we add some account balance updates
        testAddAccount()
    }

    private fun testAddAccount() {
        thread {
            db.dao().updateBalance(
                AccountDto(
                    accountBalance = 100f,
                    accountName = "investment",
                    percentChange = 0f,
                    date = System.currentTimeMillis()
                ),
                AccountDto(
                    accountBalance = 103f,
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
                )
            )
        }
    }


    fun getDb(): AccountsDatabase = db
}
