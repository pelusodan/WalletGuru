package com.peluso.walletguru.ui.recyclerview

import com.kirkbushman.araw.models.Submission
import com.kirkbushman.araw.utils.createdDate

data class SubmissionCell(
        // this is what we want to be shown on our front page. Simple for now
        val subreddit: String,
        val title: String,
        val body: String,
        val votes: Int,
        val date: String,
        val author: String
) {


    companion object {
        fun Submission.toSubmissionCell(): SubmissionCell {
            return SubmissionCell(subreddit, title, this.selfText
                    ?: "no text", score, this.createdDate.toString(), author)
        }
    }
}
