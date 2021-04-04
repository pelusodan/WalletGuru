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
import com.peluso.walletguru.ui.recyclerview.SubmissionCell
import com.peluso.walletguru.ui.recyclerview.SubmissionsRecyclerViewAdapter
import com.peluso.walletguru.viewmodel.MainViewModel

class AboutFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return inflater.inflate(R.layout.fragment_about, container, false)

    }

}