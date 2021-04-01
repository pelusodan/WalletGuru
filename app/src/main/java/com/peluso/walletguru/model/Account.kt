package com.peluso.walletguru.model

import com.kirkbushman.araw.models.Submission

/**
 * Class which represents a financial account to be tracked in our application. An account is linked
 * to certain subreddits, and based on the performance of the account these posts are prioritized
 */
class Account(val type: AccountType, val currentBalance: Float, val percentageChange: Float) {

    //TODO: make this able to be stored in a sqlite database or in sharedprefs so we can save a user's
    // balance over time

    fun List<Account>.orderSubmissions(submissions: List<Submission>): List<Submission> {
           //TODO: write the algorithm for how to order a list of submissions based on the
           // performance of the account

        return submissions
    }
}

// TODO: as we add more mandatory fields to the accounts, we'll update this function to add to the constructor
fun List<AccountDto>.toAccounts(): List<Account> {
    return this.map { Account(it.accountName.toAccountType(), it.accountBalance, it.percentChange) }

}