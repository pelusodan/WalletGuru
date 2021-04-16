package com.peluso.walletguru.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.database.AccountsDao
import com.peluso.walletguru.database.FavoritesDao
import com.peluso.walletguru.model.*
import com.peluso.walletguru.model.Account.Companion.orderSubmissions
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.model.toAccounts
import com.peluso.walletguru.reddit.RedditHelper
import com.peluso.walletguru.viewstate.MainViewState
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.round

class MainViewModel : ViewModel() {

    private lateinit var sharedPrefPutter: (String?) -> Unit?
    private lateinit var accountsDao: AccountsDao
    private lateinit var favoritesDao: FavoritesDao
    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData(MainViewState())
    val viewState: LiveData<MainViewState> = _viewState

    private var redditHelper: RedditHelper? = null

    fun initRedditHelper(context: Context, locationGetter: () -> CountryType?) {
        _viewState.postValue(_viewState.value?.copy(isLoading = true, submissions = null))
        thread {
            viewState.value?.let { state ->
                redditHelper = RedditHelper(context).apply {
                    // grabbing the location from shared prefs if we have it
                    locationGetter()?.let {
                        _viewState.postValue(_viewState.value?.copy(locationEnabled = true, countryType = it))
                    }
                    // so we don't cast to an empty list
                    if (state.userAccounts.isEmpty()) {
                        Toast.makeText(context, "Need to add accounts to get Reddit content", Toast.LENGTH_LONG).show()
                        _viewState.postValue(_viewState.value?.copy(isLoading = false, submissions = null))
                        return@thread
                    }
                    val accountTypes = state.userAccounts.map { it.type } as MutableList
                    // if we have a country, we should add it to the map
                    _viewState.value?.countryType?.let { countryType ->
                        accountTypes.add(countryType)
                    }
                    getSubmissionsFromAccountTypes(
                            *accountTypes.toTypedArray()
                    ).let { orderSubmissions(it) }
                }
            }
        }
    }

    private fun orderSubmissions(map: Map<PostType, List<Submission>>) {
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
        Log.wtf("TAG", "Favorites: \n\n\n$favorites\n\n\n")
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
            val percentChange = round(((accountBalance - lastBalance) / lastBalance * 100f) * 1000) / 1000
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
                // Toast
                Toast.makeText(null, "help", Toast.LENGTH_SHORT).show()
            } else {
                //if account doesn't exist add to balance list
                val percentageChange = 0f
                val date = System.currentTimeMillis()
                accountsDao.updateBalance(AccountDto(accountName, accountBalance, percentageChange, date))
                setAccounts()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun enableLocation(context: Context) {
        _viewState.postValue(viewState.value?.copy(locationEnabled = true))
        val locationManager = (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).also { location ->
            getCountryFromLocation(location, context)?.let {
                // if we successfully get a country
                _viewState.postValue(_viewState.value?.copy(countryType = it, locationEnabled = true))
            } ?: kotlin.run {
                // else statement for if we get no country
                Toast.makeText(context, "Country does not have location-based finance communities", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getCountryFromLocation(location: Location?, context: Context): CountryType? {
        location?.let {
            val gcd = Geocoder(context, Locale.getDefault())
            val countryType = CountryType.fromString(gcd.getFromLocation(it.latitude, it.longitude, 1)[0].countryName)?.also { type ->
                // if we do get the country, load into shared prefs
                sharedPrefPutter(type.country)
            }
            return countryType
        }
        return null
    }

    fun disableLocation() {
        viewState.value?.userAccounts?.filter { it.type is AccountType }?.let {
            _viewState.postValue(viewState.value?.copy(locationEnabled = false, countryType = null, userAccounts = it))
        }
        sharedPrefPutter(null)
    }

    fun setSharedPrefPutter(function: (String?) -> Unit) {
        sharedPrefPutter = function
    }

}
