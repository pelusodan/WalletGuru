package com.peluso.walletguru.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peluso.walletguru.R
import com.peluso.walletguru.model.SubmissionCell
import com.peluso.walletguru.model.SubmissionCell.Companion.toSubmissionCell
import com.peluso.walletguru.ui.recyclerview.SubmissionsRecyclerViewAdapter
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState

class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mainButton: FloatingActionButton
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(root)
        return root
    }

    private fun initViews(root: View) {
        mainButton = root.findViewById(R.id.main_button)
        mainButton.setOnClickListener {
            viewModel.initRedditHelper(requireContext())
        }
        mainRecyclerView = root.findViewById(R.id.main_recycler_view)
        mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mainRecyclerView.setHasFixedSize(true)
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.layoutPosition
                viewModel.removeSubmissionAt(position)
            }

        })
        itemTouchHelper.attachToRecyclerView(mainRecyclerView)
        progressBar = root.findViewById(R.id.main_progressbar)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observe(
            viewLifecycleOwner,
            Observer { viewState -> handleViewState(viewState) })
    }

    private fun handleViewState(viewState: MainViewState?) {
        viewState?.let { viewState ->
            // showing loading
            progressBar.visibility = if (viewState.isLoading) VISIBLE else GONE
            // give the submissions to our recyclerview adapter, also live updates
            viewState.submissions?.let { list ->
                val state = mainRecyclerView.layoutManager?.onSaveInstanceState()
                mainRecyclerView.adapter =
                    SubmissionsRecyclerViewAdapter(
                        // makes sure that each time we get new favorites that they are checked in the recyclerview
                        list.map { it.toSubmissionCell(viewState.favorites) },
                        { launchDetailView(it) },
                        { cell, shouldAdd ->
                            viewModel.addToFavorites(cell, shouldAdd)
                        })
                        .also {
                            it.notifyDataSetChanged()
                            state?.let { mainRecyclerView.layoutManager?.onRestoreInstanceState(it) }
                        }
            } ?: kotlin.run {
                // this is sort of like an `else` for the null check that happens above
                // in this case we will show an empty screen while we load
                mainRecyclerView.adapter = SubmissionsRecyclerViewAdapter(listOf(), {}, { _, _ -> })
            }
        }
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
