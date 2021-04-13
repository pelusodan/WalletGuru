package com.peluso.walletguru.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peluso.walletguru.R
import com.peluso.walletguru.database.LocalDatabase
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState
import java.lang.Float.parseFloat

class AddAccountFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var listView: ListView
    private lateinit var db: LocalDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_addaccount, container, false)
        initViews(root)
        viewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return root
    }

    private fun initViews(view: View) {
        val syncButton: FloatingActionButton = view.findViewById(R.id.sync_accounts_button)
        listView = view.findViewById(R.id.accounts_listview)
        syncButton.setOnClickListener {
            //TODO: make this pop up dialog to add account
            launchSubmitNewAccount()
        }
    }

    private fun launchSubmitNewAccount() {

        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.fragment_submitnewaccount, null)

        val enterAccountNameView = view.findViewById(R.id.enter_new_account_name) as EditText
        val enterAccountBalanceView = view.findViewById(R.id.enter_new_account_balance) as EditText

        val accountName = enterAccountNameView.text.toString()
        val accountBalance = parseFloat(enterAccountBalanceView.text.toString())


        builder.setPositiveButton("SUBMIT"
        ) { dialog, id ->
            db.accountsDao().updateBalance(
                    AccountDto(
                            accountBalance = accountBalance,
                            accountName = accountName,
                            percentChange = 0f,
                            date = System.currentTimeMillis()
                    ))
        }

        builder.setNegativeButton("CANCEL", null)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            handleViewState(it)
        })
    }

    private fun handleViewState(state: MainViewState?) {
        state?.let {
            //TODO: make our own view for this so we can show balance AND acct name
            listView.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.currentAccountBalances.map { it.accountName }
            )
        }
    }
}