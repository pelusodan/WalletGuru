package com.peluso.walletguru.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.model.SubmissionCell
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main recyclerview for showing posts from the reddit feed
 * TODO: refactor this to take in a click listener so we can launch the correct post in a dialog for an expanded view
 * TODO: fix the jumping to the top that occurs every time we delete or add a favorite
 */
class AccountHistoryRecyclerViewAdapter(
    val cells: List<AccountDto>
) : RecyclerView.Adapter<AccountHistoryRecyclerViewAdapter.AccountHistoryRecyclerViewHolder>() {

    class AccountHistoryRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView =
                itemView.findViewById(R.id.accountHistoryDate_Value)
        private val balanceText: TextView =
                itemView.findViewById(R.id.accountHistoryBalance_Value)
        private val percentageChangeText: TextView =
                itemView.findViewById(R.id.accountHistoryPercentage_Value)

        fun bind(
                account: AccountDto
        ) {
            dateText.text = convertLongToTime(account.date)
            balanceText.text = account.accountBalance.toString()
            percentageChangeText.text = account.percentChange.toString()

        }

        private fun convertLongToTime(date: Long): String? {
            val date = Date(date)
            val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
            return format.format(date)
        }
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): AccountHistoryRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.account_history_cell, parent, false)
        return AccountHistoryRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountHistoryRecyclerViewHolder, position: Int) {
        holder.bind(cells[position])
    }

    override fun getItemCount(): Int = cells.size

}