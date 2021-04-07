package com.peluso.walletguru.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
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
        //TOOD: launch same detailed view of the cell in the favorites page (make a class for this)

    }
}