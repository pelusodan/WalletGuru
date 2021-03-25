package com.peluso.walletguru.reddit

import android.content.Context
import android.util.Log
import com.kirkbushman.araw.RedditClient
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.R
import com.peluso.walletguru.model.AccountType
import kotlinx.coroutines.Dispatchers
import java.util.logging.Logger
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class RedditHelper(context: Context) {

    private val TAG = this.javaClass.canonicalName!!
    private var client: RedditClient?
    private var helper: AuthUserlessHelper

    init {
        // step 1 - create the helper
        helper = AuthUserlessHelper(
            context = context,
            clientId = context.getString(R.string.reddit_client_id),
            deviceId = null, // set as null to use the default android UUID
            scopes = listOf("read").toTypedArray(), // array of scopes strings
            logging = true
        )
        if (!helper.shouldLogin()) {
            // use saved one
            post("user saved one")
        } else {
            // you must authenticate
            post("you must authennticate")
        }
        // step 2 - obtain a client
        client = helper.getRedditClient()
    }

    fun postsOffMain() {
        //Dispatchers.IO.dispatch(context,)
        getPosts()
    }

    private fun getPosts() {
        // get the submissions from a subreddit
        val fetcher = client?.contributionsClient?.multiredditSubmissions(
            "finance",
            "investing",
            "creditcards"
        )
        val submissions = fetcher?.fetchNext()
        submissions?.forEach {
            post(it.toString())
        }
    }

    private fun post(msg: String) {
        Log.v(TAG, msg)
    }

    /**
     * Responsible for getting the submissions from the reddit client based on the given accounts
     * @param accounts account types to pull from the library (connected to subreddits)
     * @return a map of the accounts given to the submissions returned from the library
     */
    fun getSubmissionsFromAccountTypes(vararg accounts: AccountType): Map<AccountType, List<Submission>> {
        val map = mutableMapOf<AccountType, List<Submission>>()
        // first grab all the subreddits we need from the accounts given
        val subreddits = accounts.map { it.subreddits }.reduce { acc, list -> acc + list }
        post("TOTAL SUBREDDITS = $subreddits")
        val fetcher =
            client?.contributionsClient?.multiredditSubmissions(
                *subreddits.toTypedArray(),
                limit = 50L
            )
        val submissions = fetcher?.fetchNext()
        // now we need to unpack the posts according to their account type, so we can put them in the map
        submissions?.forEach { submission ->
            accounts.forEach { account ->
                if (submission.subreddit in account.subreddits) {
                    // add to the existing map value, if nothing exists then we make the empty list
                    map[account] = (map[account] ?: listOf()) + submission
                }
            }
        }
        // logging our results
        post(map.keys.map {
            "${it.name}\n\n" +
                    map[it]?.map { "r/${it.subreddit} : ${it.title}" }
                        ?.reduce { acc, s -> acc + "\n + $s" }
        }.reduce { acc, s -> acc + "\n $s" } ?: "${map.keys}")
        return map
    }
}

