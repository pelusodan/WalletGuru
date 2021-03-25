package com.peluso.walletguru.reddit

import android.content.Context
import android.util.Log
import com.kirkbushman.araw.RedditClient
import com.kirkbushman.araw.helpers.AuthUserlessHelper
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
            clientId = "",
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
        Log.wtf(TAG, msg)
    }

    //TODO: we need to first grab all 'submissions' and then syphon them into our custom account types
    // once we have the full collection we can modify how their popularity will affect placement

}

