package com.peluso.walletguru.model

import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.model.Account.Companion.orderSubmissions
import kotlin.math.abs

/**
 * Class which represents a financial account to be tracked in our application. An account is linked
 * to certain subreddits, and based on the performance of the account these posts are prioritized
 */
class Account(val type: AccountType, val currentBalance: Float, val percentageChange: Float) {

    companion object {
        fun List<Account>.orderSubmissions(accountMap: Map<AccountType, List<Submission>>):
                List<Submission> {
            //TODO: refactor to use the new parameters (provide mapping of account type to submission list)
            // I've kept your below code as a reference

            val sortedAccounts = this.sortedByDescending { abs(it.percentageChange) }

            val subredditRankings = sortedAccounts.map { it.type to sortedAccounts.indexOf(it) }.toMap()

            val sortedAccountMap = ArrayList<Submission>();

            subredditRankings.entries.forEach {
                if (accountMap.containsKey(it.key)) {
                    sortedAccountMap.addAll(accountMap.getValue(it.key))
                }
            }

            return sortedAccountMap
        }
    }
}

// TODO: as we add more mandatory fields to the accounts, we'll update this function to add to the constructor
fun List<AccountDto>.toAccounts(): List<Account> {
    return this.map { Account(it.accountName.toAccountType(), it.accountBalance, it.percentChange) }

}
