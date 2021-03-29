package com.peluso.walletguru.viewstate

import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.model.AccountType


data class MainViewState(
        // for now this is only the type, but eventually make it store acct type object
        val enabledAccounts: List<AccountType>? = null,
        // this is the final view list of submissions (what is displayed to in our recyclerview)
        // should be calculated from a viewmodel function based on our performance ranking
        val submissions: List<Submission>? = null,
        // use this to decide when to render the progressview on the main page
        val isLoading: Boolean = false
) {

}
