package com.peluso.walletguru.viewstate

import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.model.Account
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.model.SubmissionCell


data class MainViewState(
    // for now this is only the type, but eventually make it store acct type object
    val enabledAccounts: List<AccountType>? = null,
    // this is the final view list of submissions (what is displayed to in our recyclerview)
    // should be calculated from a viewmodel function based on our performance ranking
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
    val userAccounts: List<Account> = listOf()
) {
    fun removeSubmissionAt(position: Int): MainViewState {
        val mutableList: MutableList<Submission> = submissions as MutableList<Submission>
        if (submissions.isEmpty()) return this
        mutableList.removeAt(position)
        return this.copy(submissions = mutableList)
    }

}
