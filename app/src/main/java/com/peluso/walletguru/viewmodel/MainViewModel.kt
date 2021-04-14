package com.peluso.walletguru.viewmodel

import android.accounts.Account
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.database.AccountsDao
import com.peluso.walletguru.database.FavoritesDao
import com.peluso.walletguru.model.Account.Companion.orderSubmissions
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.model.toAccounts
import com.peluso.walletguru.reddit.RedditHelper
import com.peluso.walletguru.model.SubmissionCell
import com.peluso.walletguru.viewstate.MainViewState
import java.util.stream.Collectors
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {

    private lateinit var accountsDao: AccountsDao
    private lateinit var favoritesDao: FavoritesDao
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData(MainViewState())
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
        _viewState.value?.userAccounts?.let {
            _viewState.postValue(
                    _viewState.value?.copy(
                            submissions = it.orderSubmissions(map),
                            isLoading = false
                    )
            )
        }
    }

    fun removeSubmissionAt(position: Int) {
        _viewState.postValue(_viewState.value?.removeSubmissionAt(position))
    }

    // functions as a toggle through the heart button
    fun addToFavorites(cell: SubmissionCell, bool: Boolean) {
        thread {
            if (bool) {
                favoritesDao.addFavorite(cell.copy(isFavorited = bool))
            } else {
                favoritesDao.removeFavorite(cell)
            }
            setFavorites()
        }
    }

    fun setDatabase(accountsDao: AccountsDao, favoritesDao: FavoritesDao) {
        this.accountsDao = accountsDao
        this.favoritesDao = favoritesDao
        // as soon as we get the user dao, we update our viewstate to hold the user's accounts in a map
        // also update favorites to match local database
        initViewState()
    }

    /**
     * enforces no concurrency errors
     */
    private fun initViewState() {
        thread {
            setFavorites()
            setAccounts()
        }
    }

    private fun setFavorites() {
        val favorites = favoritesDao.getAllFavorites()
        _viewState.postValue(viewState.value?.copy(favorites = favorites))
        Log.wtf("TAG", "Favorites: \n\n\n" + favorites.toString() + "\n\n\n")
    }

    private fun setAccounts() {
        val allBalances = accountsDao.getAllAccounts()
        val mostRecentAccountBalances = accountsDao.getMostRecentAccountBalances()
        Log.wtf("TAG", allBalances.map { it.toString() + "\n\n" }.reduce { acc, s -> acc + s })
        _viewState.postValue(
                viewState.value?.copy(
                        currentAccountBalances = mostRecentAccountBalances,
                        ledger = allBalances,
                        userAccounts = mostRecentAccountBalances.toAccounts()
                )
        )
    }

    fun updateAccountBalance(accountName: String, accountBalance: Float, date: Long) {
        thread {
            val currentBalances = accountsDao.getMostRecentAccountBalances()
            val lastBalance = currentBalances[currentBalances.map { it.accountName }.indexOf(accountName)].accountBalance
            val percentChange = (accountBalance - lastBalance) / lastBalance * 100f
            accountsDao.updateBalance(AccountDto(accountName, accountBalance, percentChange, date))
            // updating our viewstate
            setAccounts()
        }
    }

    fun addNewAccount(accountName: String, accountBalance: Float) {
        thread {
            val accounts = accountsDao.getAllAccounts()
            val accountNames = ArrayList<String>()

            //build a unique list of account names
            for (account in accounts) {
                if (!accountNames.contains(account.accountName)) {
                    accountNames.add(account.accountName)
                }
            }

            //check if new account being added already exist in the list
            // if account already exist show toast
            if (accountNames.contains(accountName)) {
                //TODO: show toast saying account already exists
            } else {
                //if account doesnt exist add to balance list
                val percentageChange = 0f;
                val date = System.currentTimeMillis()
                accountsDao.updateBalance(AccountDto(accountName, accountBalance, percentageChange, date))
                setAccounts()
            }
        }
    }

}
