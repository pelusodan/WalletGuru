package com.peluso.walletguru.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountDto(
    @ColumnInfo(name = "account_name") val accountName: String,
    @ColumnInfo(name = "account_balance") val accountBalance: Float,
    @ColumnInfo(name = "percent_change") val percentChange: Float,
    @ColumnInfo(name = "date") val date: Long
) {
    @PrimaryKey(autoGenerate = true)
    var accountId: Int = 0
}
