package com.peluso.walletguru.model

import com.kirkbushman.araw.models.Submission

/**
 * Class which represents a financial account to be tracked in our application. An account is linked
 * to certain subreddits, and based on the performance of the account these posts are prioritized
 */
class Account(val type: AccountType, val currentBalance: Float, val percentageChange: Float) {


    companion object {
        fun List<Account>.orderSubmission(accountMap: Map<AccountType, List<Submission>>) {
            //TODO: refactor to use the new parameters (provide mapping of account type to submission list)
            // I've kept your below code as a reference
        }

        fun List<Account>.orderSubmissions(
            submissions: List<Submission>,
            accounts: List<Account>
        ): List<Submission> {

            //list of accounts sorted in order of decreasing percentage change
            val sortedAccounts = accounts.sortedByDescending { it.percentageChange }

            //map ranked accounts index to account type
            val subredditRankings = sortedAccounts.map { it.type.toString() to sortedAccounts.indexOf(it) }.toMap()

            //compator using indexes of subreddits in the map
            val comparator = Comparator<Submission> {submission1, submission2
            ->
                when {
                    subredditRankings.getValue(submission1.subreddit) > subredditRankings.getValue(submission2.subreddit) -> 1
                    subredditRankings.getValue(submission1.subreddit) < subredditRankings.getValue(submission2.subreddit) -> -1
                    else -> 0
                }
            }

            // it.subreddit to subredditRankings.getValue(it.subreddit)
            return submissions.sortedWith(comparator)
        }
    }
}

// TODO: as we add more mandatory fields to the accounts, we'll update this function to add to the constructor
fun List<AccountDto>.toAccounts(): List<Account> {
    return this.map { Account(it.accountName.toAccountType(), it.accountBalance, it.percentChange) }

}
