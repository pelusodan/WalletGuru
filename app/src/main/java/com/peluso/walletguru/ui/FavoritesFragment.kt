package com.peluso.walletguru.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R
import com.peluso.walletguru.model.SubmissionCell
import com.peluso.walletguru.ui.recyclerview.SubmissionsRecyclerViewAdapter
import com.peluso.walletguru.viewmodel.MainViewModel

class FavoritesFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyFavoritesLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorites, container, false)
        initViews(root)
        return root
    }

    private fun initViews(root: View) {
        emptyFavoritesLayout = root.findViewById(R.id.empty_favorites_layout)
        recyclerView = root.findViewById(R.id.favorites_recyclerview)
        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            val scrollState = recyclerView.layoutManager?.onSaveInstanceState()
            state.favorites.let { list ->
                recyclerView.adapter = SubmissionsRecyclerViewAdapter(
                    list,
                    { launchDetailView(it) },
                    { cell, shouldAdd ->
                        viewModel.addToFavorites(cell, shouldAdd)
                    })
                    .also {
                        it.notifyDataSetChanged()
                        scrollState?.let { recyclerView.layoutManager?.onRestoreInstanceState(it) }
                    }
                showEmptyFavoritesMessage(list.isEmpty())
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showEmptyFavoritesMessage(empty: Boolean) {
        emptyFavoritesLayout.visibility = if (empty) VISIBLE else GONE
    }

    private fun launchDetailView(cell: SubmissionCell) {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.detail_submission_cell, null)
        builder.setView(view)
        val title = view.findViewById<TextView>(R.id.title_textview)
        val subreddit = view.findViewById<TextView>(R.id.subreddit_textview)
        val author = view.findViewById<TextView>(R.id.author_textview)
        val body = view.findViewById<TextView>(R.id.body_textview)
        val votes = view.findViewById<TextView>(R.id.votes_textview)
        val favorite = view.findViewById<ToggleButton>(R.id.favorite_button)
        val urlView = view.findViewById<View>(R.id.detail_webview_layout)
        val webView = view.findViewById<WebView>(R.id.detail_webview)
        cell.let { cell ->
            title.text = cell.title
            subreddit.text = cell.subreddit
            author.text = cell.author
            body.text = cell.body
            votes.text = cell.votes.toString() + " ↑"
            favorite.isChecked = cell.isFavorited
            favorite.setOnClickListener {
                viewModel.addToFavorites(cell, favorite.isChecked)
            }
            cell.url?.let {
                urlView.visibility = VISIBLE
                webView.apply {
                    loadUrl(it)
                    setInitialScale(75)
                    settings.builtInZoomControls = true
                    settings.javaScriptEnabled = true
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}