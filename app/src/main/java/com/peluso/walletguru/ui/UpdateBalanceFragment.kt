package com.peluso.walletguru.ui

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R
import com.peluso.walletguru.model.Account
import com.peluso.walletguru.model.AccountType
import com.peluso.walletguru.ui.recyclerview.AccountHistoryRecyclerViewAdapter
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState
import java.lang.Exception
import java.text.NumberFormat


class UpdateBalanceFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var spinner: Spinner
    private lateinit var selectedAccount: String
    private lateinit var accountRecyclerView: RecyclerView
    private lateinit var newBalanceText: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_updatebalance, container, false)
        selectedAccount = "N/A"

        initViews(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            handleViewState(it)
        })
        newBalanceText = view.findViewById(R.id.newBalance)
        spinner = view.findViewById(R.id.spinnerAccountType)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    selectedAccount = parent.getItemAtPosition(position).toString()
                }
            }
        }
        view.findViewById<View>(R.id.submit_newBalance).setOnClickListener {
            viewModel.updateAccountBalance(
                accountName = selectedAccount,
                accountBalance = newBalanceText.text.getFloatValue(),
                date = System.currentTimeMillis()
            )
        }
        // so we only set the dropdown options once per screen loading
        viewModel.viewState.value?.userAccounts?.let {
            setupDropdownOptions(it)
        }
    }

    private fun setupDropdownOptions(options: List<Account>) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            options.filter { it.type is AccountType }.sortedBy { it.type.tableName }.toMutableList()
                .map { it.type.tableName }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
    }

    private fun initViews(root: View) {
        accountRecyclerView = root.findViewById(R.id.accountHistory_recycler_view)
        accountRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun handleViewState(state: MainViewState) {
        state.ledger.let { ledger ->
            accountRecyclerView.adapter =
                AccountHistoryRecyclerViewAdapter(ledger.reversed()).also {
                    it.notifyDataSetChanged()
                }
        }
    }
}

private fun Editable.getFloatValue(): Float {
    return try {
        NumberFormat.getInstance().parse(toString())!!.toFloat()
    } catch (e: Exception) {
        Log.wtf("MAIN", "Failed to convert edit text value to float")
        0f
    }
}
