package com.peluso.walletguru.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peluso.walletguru.R
import com.peluso.walletguru.database.LocalDatabase
import com.peluso.walletguru.model.AccountDto
import com.peluso.walletguru.ui.recyclerview.AccountHistoryRecyclerViewAdapter
import com.peluso.walletguru.ui.recyclerview.SubmissionsRecyclerViewAdapter
import com.peluso.walletguru.viewmodel.MainViewModel
import com.peluso.walletguru.viewstate.MainViewState

class UpdateBalanceFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var spinner: Spinner
    private lateinit var selectedAccount: String
    private lateinit var accountRecyclerView: RecyclerView
    private lateinit var db: LocalDatabase
    private var newBalanceText: EditText? = null



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_updatebalance, container, false)
        val textView: TextView = root.findViewById(R.id.text_accountName)
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

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    selectedAccount = parent.getItemAtPosition(position).toString()
                }
            }
        }


        view.findViewById<View>(R.id.submit_newBalance).setOnClickListener {
            Toast.makeText(activity, selectedAccount, Toast.LENGTH_LONG).show()
            db.accountsDao().updateBalance(
                     AccountDto(
                             accountBalance = newBalanceText.getText(),
                             accountName = selectedAccount,
                             percentChange = 0f,
                             date = System.currentTimeMillis()
                     ))
        }

    }

    private fun initViews(root: View) {
        accountRecyclerView = root.findViewById(R.id.accountHistory_recycler_view)
        accountRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        accountRecyclerView.setHasFixedSize(true)
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
                return
            }
        })
        itemTouchHelper.attachToRecyclerView(accountRecyclerView)
    }


    private fun handleViewState(state: MainViewState?) {
        state?.userAccounts?.let {
            // fill spinner with (it)
            ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.account_SpinnerArrays,
                    android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }

        state?.let { viewState ->
              // give the currentAccountBalances to our recyclerview adapter, also live updates
            viewState.currentAccountBalances?.let { list ->
                val state = accountRecyclerView.layoutManager?.onSaveInstanceState()
                accountRecyclerView.adapter =
                        AccountHistoryRecyclerViewAdapter(list)
                                .also {
                                    it.notifyDataSetChanged()
                                    state?.let { accountRecyclerView.layoutManager?.onRestoreInstanceState(it) }
                                }
            } ?: kotlin.run {
                // this is sort of like an `else` for the null check that happens above
                // in this case we will show an empty screen while we load
                accountRecyclerView.adapter = SubmissionsRecyclerViewAdapter(listOf(), {}, { _, _ -> })
            }
        }

    }
}
