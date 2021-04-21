package com.peluso.walletguru.ui.recyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R
import com.peluso.walletguru.model.SubmissionCell

/**
 * Main recyclerview for showing posts from the reddit feed
 * TODO: refactor this to take in a click listener so we can launch the correct post in a dialog for an expanded view
 * TODO: fix the jumping to the top that occurs every time we delete or add a favorite
 */
class SubmissionsRecyclerViewAdapter(
    val cells: List<SubmissionCell>,
    val onPostClick: (SubmissionCell) -> Unit,
    val onFavoriteClick: (SubmissionCell, Boolean) -> Unit
) : RecyclerView.Adapter<SubmissionsRecyclerViewAdapter.SubmissionsRecyclerViewHolder>() {

    class SubmissionsRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView =
            itemView.findViewById(R.id.title_textview)
        private val bodyText: TextView =
            itemView.findViewById(R.id.body_textview)
        private val votesText: TextView =
            itemView.findViewById(R.id.votes_textview)
        private val subreditText: TextView =
            itemView.findViewById(R.id.subreddit_textview)
        private val authorText: TextView =
            itemView.findViewById(R.id.author_textview)
        private val favortieToggle: ToggleButton =
            itemView.findViewById(R.id.favorite_button)
        private val background: ConstraintLayout =
            itemView.findViewById(R.id.relativeLayout)

        fun bind(
            submission: SubmissionCell,
            onPostClick: (SubmissionCell) -> Unit,
            onFavoriteClick: (SubmissionCell, Boolean) -> Unit
        ) {
            if (submission.isLocationBased) {
                background.setBackgroundColor(
                    Color.valueOf(0f / 255f, 250f / 255f, 130f / 255f).toArgb()
                )
            } else {
                background.setBackgroundColor(
                    Color.valueOf(255f / 255f, 87f / 255f, 34f / 255f).toArgb()
                )
            }
            titleText.text = submission.title
            bodyText.text = submission.body
            votesText.text = submission.votes.toString() + " ↑"
            subreditText.text = submission.subreddit
            authorText.text = submission.author
            itemView.setOnClickListener {
                onPostClick(submission)
            }
            favortieToggle.isChecked = submission.isFavorited
            favortieToggle.setOnClickListener {
                onFavoriteClick(submission, favortieToggle.isChecked)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubmissionsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.submission_cell, parent, false)
        return SubmissionsRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = cells.size

    override fun onBindViewHolder(holder: SubmissionsRecyclerViewHolder, position: Int) {
        holder.bind(cells[position], onPostClick, onFavoriteClick)
    }
}