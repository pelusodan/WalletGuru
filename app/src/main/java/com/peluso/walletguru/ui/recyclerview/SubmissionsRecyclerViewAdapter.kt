package com.peluso.walletguru.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R

class SubmissionsRecyclerViewAdapter(val cells: List<SubmissionCell>) : RecyclerView.Adapter<SubmissionsRecyclerViewAdapter.SubmissionsRecyclerViewHolder>() {

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

        fun bind(submission: SubmissionCell) {
            titleText.text = submission.title
            bodyText.text = submission.body
            votesText.text = submission.votes.toString() + " ↑"
            subreditText.text = submission.subreddit
            authorText.text = submission.author
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.submission_cell, parent, false)
        return SubmissionsRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = cells.size

    override fun onBindViewHolder(holder: SubmissionsRecyclerViewHolder, position: Int) {
        holder.bind(cells[position])
    }
}