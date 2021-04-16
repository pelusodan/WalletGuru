package com.peluso.walletguru.model

/***
 * This is an abstraction so we can add country types to the main sorting method without having to
 * change our mapping algorithm drastically
 */
interface PostType {
    val subreddits: List<String>
    val tableName: String
}