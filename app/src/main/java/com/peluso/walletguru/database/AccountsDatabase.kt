package com.peluso.walletguru.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peluso.walletguru.model.AccountDto

@Database(entities = [AccountDto::class], version = 1)
abstract class AccountsDatabase : RoomDatabase() {
    abstract fun dao(): AccountsDao
}
