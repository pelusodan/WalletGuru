package com.peluso.walletguru.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.reddit.RedditHelper
import com.peluso.walletguru.viewstate.MainViewState
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()
    val viewState: LiveData<MainViewState> = _viewState

    private var redditHelper: RedditHelper? = null

    fun initRedditHelper(context: Context) {
        thread {
            if (_viewState.value == null) {
                _viewState.postValue(MainViewState(isLoading = true))
                redditHelper = RedditHelper(context).apply {
                    getSubmissionsFromAccountTypes(AccountType.INVESTMENT, AccountType.CRYPTO, AccountType.REALESTATE)?.let { orderSubmissions(it) }
                }
            }
        }
    }

    private fun orderSubmissions(map: Map<AccountType, List<Submission>>) {
        //TODO: make this based on actual values and standings of the accounts. We should probably
        // make this a dynamically calculated ranking based on your input (which should use the same viewmodel)
        // for now I'll make it loop through all of them and add it to the viewstate
        val reduced = map.values.reduce { acc, list -> acc + list }
        _viewState.postValue(_viewState.value?.copy(submissions = reduced, isLoading = false))
    }

}