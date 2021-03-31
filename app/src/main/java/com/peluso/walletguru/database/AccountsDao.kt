package com.peluso.walletguru.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.peluso.walletguru.model.AccountDto

@Dao
interface AccountsDao {

    @Query("SELECT * FROM accountDto")
    fun getAllAccounts(): List<AccountDto>

    @Insert
    fun updateBalance(vararg accounts: AccountDto)

    @Query("SELECT * FROM accountDto WHERE account_name = :accountName")
    fun getLedgerFromAccount(accountName: String): List<AccountDto>

}
