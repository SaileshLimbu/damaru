package com.powersoft.damaru.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.AccountEntity
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.repository.UserRepo
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaru.R
import com.powersoft.damaru.adapters.AccountsAdapter
import com.powersoft.damaru.databinding.FragmentAccountsBinding
import com.powersoft.damaru.ui.AccountDetailActivity
import com.powersoft.damaru.ui.AddAccountActivity
import com.powersoft.damaru.viewmodels.AccountsFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountsFragment : Fragment(R.layout.fragment_accounts) {
    private val vm: AccountsFragmentViewModel by viewModels()
    private var _binding: FragmentAccountsBinding? = null
    private val b get() = _binding!!
    private val addAccountResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                vm.getAllAccounts()
            }
        }

    private val accountDetailResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                vm.getAllAccounts()
            }
        }

    @Inject
    lateinit var userRepo: UserRepo

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.swipeRefresh.setOnRefreshListener {
            vm.getAllAccounts()
        }

        b.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    b.extendedFAB.shrink()
                } else if (dy < 0) {
                    b.extendedFAB.extend()
                }
            }
        })

        if (userRepo.seasonEntity.value?.isRootUser == true) {
            b.extendedFAB.visibility = View.VISIBLE
        } else {
            b.extendedFAB.visibility = View.GONE
        }
        b.extendedFAB.setOnClickListener {
            addAccountResultLauncher.launch(Intent(context, AddAccountActivity::class.java))
        }

        b.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }

        vm.allAccounts.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    b.swipeRefresh.isRefreshing = false
                    val accountAdapter = AccountsAdapter(userRepo.seasonEntity.value?.isRootUser ?: false, it.data, object : RecyclerViewItemClickListener<AccountEntity> {
                        override fun onItemClick(viewId: Int, position: Int, data: AccountEntity) {
                            when (viewId) {
                                R.id.imgDelete -> {
                                    context?.let { context ->
                                        AlertHelper.showAlertDialog(
                                            context, title = getString(R.string.delete_account) + " ??",
                                            message = getString(R.string.are_you_sure_you_want_to_delete_this_account),
                                            positiveButtonText = getString(R.string.delete),
                                            negativeButtonText = getString(R.string.cancle),
                                            onPositiveButtonClick = {
                                                vm.deleteAccount(data.id!!, object : ResponseCallback {
                                                    override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                                                        AlertHelper.showAlertDialog(
                                                            context, title = errorResponse?.message?.error ?: getString(R.string.error),
                                                            message = errorResponse?.message?.message ?: getString(R.string.error),
                                                        )
                                                    }
                                                })
                                            }
                                        )
                                    }
                                }

                                R.id.imgEdit ->{
                                    accountDetailResultLauncher.launch(Intent(context, AddAccountActivity::class.java).putExtra("account", gson.toJson(data)))
                                }

                                else -> {
                                    accountDetailResultLauncher.launch(Intent(context, AccountDetailActivity::class.java).putExtra("account", gson.toJson(data)))
                                }
                            }
                        }
                    })
                    b.recyclerView.adapter = accountAdapter

                    b.loader.root.visibility = View.GONE
                    b.errorView.root.visibility = View.GONE
                }

                is ResponseWrapper.Error -> {
                    b.swipeRefresh.isRefreshing = false
                    b.loader.root.visibility = View.GONE
                    b.errorView.tvError.text = it.errorResponse.message?.message
                    b.errorView.root.visibility = View.VISIBLE
                }

                is ResponseWrapper.Loading -> {
                    b.loader.root.visibility = View.VISIBLE
                    b.errorView.root.visibility = View.GONE
                }
            }
        }
    }
}