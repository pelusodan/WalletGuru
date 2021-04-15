package com.peluso.walletguru.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peluso.walletguru.R
import com.peluso.walletguru.model.Account
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState
import java.lang.Float.parseFloat

class AddAccountFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var listView: ListView
    private lateinit var spinner: Spinner
    private lateinit var newBalanceText: EditText
    private lateinit var accountName: String
    private lateinit var accountBalance: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_addaccount, container, false)
        accountName = "TBD"
        accountBalance = "0"
        initViews(root)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            handleViewState(it)
        })
    }

    private fun initViews(view: View) {
        val syncButton: FloatingActionButton = view.findViewById(R.id.sync_accounts_button)
        listView = view.findViewById(R.id.accounts_listview)
        syncButton.setOnClickListener {
            if (getAvaliableAccountOptions(viewModel.viewState.value!!.userAccounts).isNotEmpty()) {
                launchSubmitNewAccount()
            } else {
                Toast.makeText(
                    activity,
                    "Max number of accounts added!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun launchSubmitNewAccount() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Create New Account")

        val view = layoutInflater.inflate(R.layout.fragment_submitnewaccount, null)

        spinner = view.findViewById(R.id.enter_new_account_name)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    accountName = parent.getItemAtPosition(position).toString()
                }
            }
        }
        newBalanceText = view.findViewById(R.id.enter_new_account_balance)

        builder.setPositiveButton("SUBMIT"
        ) { dialog, id ->

            accountBalance = newBalanceText.text.toString()

            if (accountName.isNotEmpty() && accountBalance.isNotEmpty()) {
                Toast.makeText(
                    activity,
                    "Account $accountName with Balace $accountBalance added",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.addNewAccount(
                    accountBalance = parseFloat(accountBalance),
                    accountName = accountName
                )
            } else if (accountBalance.isEmpty()) {
                Toast.makeText(
                    activity,
                    "No valid Balance entered",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        builder.setNegativeButton("CANCEL", null)

        viewModel.viewState.value?.userAccounts?.let {
            setupDropdownOptions(it)
        }

        builder.setView(view)
        builder.create().show()
    }

    private fun getAvaliableAccountOptions(myAccounts: List<Account>) : List<String> {

        val optionAccountNames = ArrayList<String>()

        //build a unique list of account names
        for (account in myAccounts) {
            if (!optionAccountNames.contains(account.type.tableName)) {
                optionAccountNames.add(account.type.tableName)
            }
        }

        val optionsNotUsed = ArrayList<String>()
        val allNames = AccountType.getAllTypes()

        for (name in allNames) {
            if (!optionAccountNames.contains(name)) {
                optionsNotUsed.add(name)
            }
        }

        optionsNotUsed.sort()
        return optionsNotUsed
    }

    private fun setupDropdownOptions(options: List<Account>) {

        val optionsNotUsed = getAvaliableAccountOptions(options)
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            optionsNotUsed
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinner.adapter = adapter
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