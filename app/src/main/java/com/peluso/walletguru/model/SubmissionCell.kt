package com.peluso.walletguru.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.utils.createdDate

@Entity
data class SubmissionCell(
    // this is what we want to be shown on our front page. Simple for now
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "subreddit") val subreddit: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "votes") val votes: Int,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "isFavorited") val isFavorited: Boolean,
    @ColumnInfo(name = "url") val url: String?,
    @ColumnInfo(name = "isLocationBased") val isLocationBased: Boolean
) {

    companion object {
        fun Submission.toSubmissionCell(favorites: List<SubmissionCell>): SubmissionCell {
            // this will tell us if we need to include a webview in the details
            val url = if (domain.contains("self")) null else url
            // cross check if it's in favorites, and if so we check it
            return SubmissionCell(
                title.hashCode(),
                subreddit,
                title,
                this.selfText
                    ?: "no text",
                score,
                this.createdDate.toString(),
                author,
                favorites.map { it.title }.contains(title),
                url,
                subreddit in CountryType.allSubreddits
            )
        }
    }
}
