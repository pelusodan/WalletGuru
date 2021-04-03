package com.peluso.walletguru.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.database.AccountsDao
import com.peluso.walletguru.model.Account.Companion.orderSubmissions
import com.peluso.walletguru.model.AccountDto
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
        //TODO: make this based on actual values and standings of the accounts. We should probably
        // make this a dynamically calculated ranking based on your input (which should use the same viewmodel)
        // for now I'll make it loop through all of them and add it to the viewstate
        //val reduced = map.values.reduce { acc, list -> acc + list }
        //_viewState.postValue(_viewState.value?.copy(submissions = reduced, isLoading = false))

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
            val balances = mDao.getAllAccounts()
            val orderedBalances = orderBalances(balances)
            Log.wtf("TAG", balances.map { it.toString() + "\n\n" }.reduce { acc, s -> acc + s })
            // FIRST VIEWSTATE UPDATE - WILL NOT BE NULL AFTER THIS POINT (should use copy)
            _viewState.postValue(
                MainViewState(
                    currentAccountBalances = orderedBalances,
                    ledger = balances,
                    userAccounts = orderedBalances.toAccounts()
                )
            )
        }
    }

    private fun orderBalances(balances: List<AccountDto>): List<AccountDto> {
        val map = mutableMapOf<String, AccountDto>()
        balances.forEach { currBalance ->
            map[currBalance.accountName]?.let { max ->
                if (currBalance.date > max.date) map[currBalance.accountName] = currBalance
            } ?: kotlin.run {
                map[currBalance.accountName] = currBalance
            }
        }
        return map.values.toList()
    }

}