package com.peluso.walletguru.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.model.SubmissionCell

@Database(entities = [AccountDto::class, SubmissionCell::class], version = 3)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun accountsDao(): AccountsDao
    abstract fun favoritesDao(): FavoritesDao
}
