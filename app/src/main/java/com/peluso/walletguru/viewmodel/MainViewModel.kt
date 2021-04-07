package com.peluso.walletguru.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.database.AccountsDao
import com.peluso.walletguru.model.Account.Companion.orderSubmissions
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.model.toAccounts
import com.peluso.walletguru.reddit.RedditHelper
import com.peluso.walletguru.ui.recyclerview.SubmissionCell
import com.peluso.walletguru.viewstate.MainViewState
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private lateinit var mDao: AccountsDao
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()
    val viewState: LiveData<MainViewState> = _viewState

    private var redditHelper: RedditHelper? = null

    fun initRedditHelper(context: Context) {
        _viewState.postValue(_viewState.value?.copy(isLoading = true, submissions = null))
        thread {
            viewState.value?.let { state ->
                redditHelper = RedditHelper(context).apply {
                    getSubmissionsFromAccountTypes(
                            *state.userAccounts.map { it.type }.toTypedArray()
                    ).let { orderSubmissions(it) }
                }
            }
        }
    }

    private fun orderSubmissions(map: Map<AccountType, List<Submission>>) {
        _viewState.value?.userAccounts.let {
            _viewState.postValue(_viewState.value?.copy(submissions = it?.orderSubmissions(map), isLoading = false))
        }
    }

    fun removeSubmissionAt(position: Int) {
        _viewState.value?.let { state ->
            _viewState.postValue(state.removeSubmissionAt(position))
        }
    }

    fun addToFavorites(cell: SubmissionCell, bool: Boolean) {
        _viewState.postValue(_viewState.value!!.addToFavorites(cell, bool))
    }

    fun setDatabase(dao: AccountsDao) {
        mDao = dao
        // as soon as we get the user dao, we update our viewstate to hold the user's accounts in a map
        setAccounts()
    }

    private fun setAccounts() {
        thread {
            val allBalances = mDao.getAllAccounts()
            val mostRecentAccountBalances = mDao.getMostRecentAccountBalances()
            Log.wtf("TAG", allBalances.map { it.toString() + "\n\n" }.reduce { acc, s -> acc + s })
            // FIRST VIEWSTATE UPDATE - WILL NOT BE NULL AFTER THIS POINT (should use copy)
            _viewState.postValue(
                    MainViewState(
                            currentAccountBalances = mostRecentAccountBalances,
                            ledger = allBalances,
                            userAccounts = mostRecentAccountBalances.toAccounts()
                    )
            )
        }
    }

}
