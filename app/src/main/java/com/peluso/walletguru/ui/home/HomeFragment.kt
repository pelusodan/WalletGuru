package com.peluso.walletguru.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.peluso.walletguru.R
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.reddit.RedditHelper
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        val mainButton: Button = root.findViewById(R.id.main_button)
        mainButton.setOnClickListener {
            // TODO: set up the viewmodel to call these functions asynchronusly and use viewstates to populate each screen
            thread {
                val reddit = RedditHelper(requireContext())
                //reddit.postsOffMain()
                // testing the submissions function
                reddit.getSubmissionsFromAccountTypes(
                    AccountType.CHECKING,
                    AccountType.CREDIT_CARD,
                    AccountType.INVESTMENT
                )
            }
        }
        return root
    }
}