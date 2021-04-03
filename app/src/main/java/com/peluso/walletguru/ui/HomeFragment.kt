package com.peluso.walletguru.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R
import com.peluso.walletguru.ui.recyclerview.SubmissionCell
import com.peluso.walletguru.ui.recyclerview.SubmissionCell.Companion.toSubmissionCell
import com.peluso.walletguru.ui.recyclerview.SubmissionsRecyclerViewAdapter
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState

class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mainButton: Button
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
        //TODO: make this launch an expanded view of the submission
    }

}
