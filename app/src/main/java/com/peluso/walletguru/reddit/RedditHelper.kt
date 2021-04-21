package com.peluso.walletguru.reddit

import android.content.Context
import android.util.Log
import com.kirkbushman.araw.RedditClient
import com.kirkbushman.araw.helpers.AuthUserlessHelper
import com.kirkbushman.araw.models.Submission
import com.peluso.walletguru.R
import com.peluso.walletguru.model.PostType

class RedditHelper(context: Context) {

    private val logTag = this.javaClass.canonicalName!!
    private var client: RedditClient?
    private val helper: AuthUserlessHelper = AuthUserlessHelper(
            context = context,
            clientId = context.getString(R.string.reddit_client_id),
            deviceId = null, // set as null to use the default android UUID
            scopes = listOf("read").toTypedArray(), // array of scopes strings
            logging = true
    )

    init {
        // step 1 - create the helper
        if (!helper.shouldLogin()) {
            // use saved one
            logger("user saved one")
        } else {
            // you must authenticate
            logger("you must authennticate")
        }
        // step 2 - obtain a client
        client = helper.getRedditClient()
    }

    private fun logger(msg: String) {
        Log.v(logTag, msg)
    }

    /**
     * Responsible for getting the submissions from the reddit client based on the given accounts
     * @param accounts account types to pull from the library (connected to subreddits)
     * @return a map of the accounts given to the submissions returned from the library
     */
    fun getSubmissionsFromAccountTypes(vararg accounts: PostType): Map<PostType, List<Submission>> {
        val map = mutableMapOf<PostType, List<Submission>>()
        if (accounts.isEmpty()) {
            logger("No account types given!")
            return map
        }
        // first grab all the subreddits we need from the accounts given
        val subreddits = accounts.map { it.subreddits }.reduce { acc, list -> acc + list }
        logger("TOTAL SUBREDDITS = $subreddits")
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
        logger(map.keys.map { postType ->
            "${postType}\n\n" +
                    map[postType]?.map { "r/${it.subreddit} : ${it.title}" }
                            ?.reduce { acc, s -> "$acc\n + $s" }
        }.reduce { acc, s -> "$acc\n $s" })
        return map
    }
}

