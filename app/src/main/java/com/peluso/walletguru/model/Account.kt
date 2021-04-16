package com.peluso.walletguru.model

import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.model.Account.Companion.orderSubmissions
import kotlin.math.abs

/**
 * Class which represents a financial account to be tracked in our application. An account is linked
 * to certain subreddits, and based on the performance of the account these posts are prioritized
 */
class Account(val type: PostType, val currentBalance: Float, val percentageChange: Float) {

    companion object {
        fun List<Account>.orderSubmissions(accountMap: Map<PostType, List<Submission>>):
                List<Submission> {
            // getting the country from the map, and if we find a country adding it to the account sorting
            val countryPostType = accountMap.keys.find { it is CountryType }
            val sortedAccounts = this as MutableList
            countryPostType?.let {
                sortedAccounts.add(Account(it, 0f, 20f))
            }
            sortedAccounts.sortedByDescending { abs(it.percentageChange) }
            val subredditRankings = sortedAccounts.map { it.type to sortedAccounts.indexOf(it) }.toMap()

            // show all first posts in order of priority accounts, then all second posts
            // in order of priority accounts, then all third posts and so forth
            val sortedAccountMap = LinkedHashMap<PostType, List<Submission>>()

            subredditRankings.entries.forEach {
                if (accountMap.containsKey(it.key)) {
                    sortedAccountMap[it.key] = accountMap.getValue(it.key)
                }
            }

            var maxLoopCounter = 0

            // loop through map to find max loop counter for building sorted array
            sortedAccountMap.entries.forEach {
                val value = sortedAccountMap.getValue(it.key).size
                if (value > maxLoopCounter) {
                    maxLoopCounter = value
                }
            }

            val sortedArray = ArrayList<Submission>();

            for (i in 0..maxLoopCounter) {
                sortedAccountMap.entries.forEach {
                    if (i < it.value.size)
                        sortedArray.add(it.value[i])
                }
            }

            return sortedArray
        }
    }
}

// TODO: as we add more mandatory fields to the accounts, we'll update this function to add to the constructor
fun List<AccountDto>.toAccounts(): List<Account> {
    return this.map { Account(it.accountName.toAccountType(), it.accountBalance, it.percentChange) }

}
