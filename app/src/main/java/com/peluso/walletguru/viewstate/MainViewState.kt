package com.peluso.walletguru.viewstate

import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.model.*


data class MainViewState(
    // this is the final view list of reddit posts (what is displayed to in our recyclerview)
    val submissions: List<Submission>? = null,
    // use this to decide when to render the progressview on the main page
    val isLoading: Boolean = false,
    // to track the current favorites of the user (should import from local database)
    val favorites: List<SubmissionCell> = listOf(),
    // to track the account  information for the left screen (accounts, balances)
    val currentAccountBalances: List<AccountDto> = listOf(),
    // to track the logged transactions the user has made
    val ledger: List<AccountDto> = listOf(),
    // MAIN ACCOUNT LIST - this is where we get the proper account information to mess with
    val userAccounts: List<Account> = listOf(),
    // to keep track of whether or not we're providing location-based subreddits
    val locationEnabled: Boolean = false,
    // to keep track of our location type
    val countryType: CountryType? = null,
    // holding error message no accounts added
    val hasNoAccounts: String? = null
) {
    fun removeSubmissionAt(position: Int): MainViewState {
        val mutableList: MutableList<Submission> = submissions as MutableList<Submission>
        if (submissions.isEmpty()) return this
        mutableList.removeAt(position)
        return this.copy(submissions = mutableList)
    }

}
