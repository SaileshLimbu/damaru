package com.powersoft.damaruadmin.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.DeviceEntity
import com.powersoft.common.model.PickerEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.State
import com.powersoft.common.model.UserEntity
import com.powersoft.common.ui.PickerActivity
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.damaruadmin.R
import com.powersoft.damaruadmin.adapters.UserAdapter
import com.powersoft.damaruadmin.databinding.FragmentHomeBinding
import com.powersoft.damaruadmin.ui.AddUserActivity
import com.powersoft.damaruadmin.ui.AdminMainActivity
import com.powersoft.damaruadmin.ui.UserDetailActivity
import com.powersoft.damaruadmin.viewmodels.AdminHomeFragmentViewModel
import com.powersoft.damaruadmin.viewmodels.AdminMainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminUsersFragment : Fragment(R.layout.fragment_home), RecyclerViewItemClickListener<UserEntity>, AdminMainActivity.SearchableFragment {
    private var _binding: FragmentHomeBinding? = null
    private val b get() = _binding!!
    private val userAdapter by lazy { createUserAdapter() }
    private val vm: AdminHomeFragmentViewModel by viewModels()
    private var userIdToAssign: String? = null
    private val actViewModel: AdminMainActivityViewModel by activityViewModels()

    @Inject
    lateinit var gson: Gson

    private val addUserForResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            vm.getAllMyUsers()
        }
    }

    private val userDetailResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            vm.getAllMyUsers()
            if (result.data?.getBooleanExtra("deviceUpdate", false) == true) {
                actViewModel.refreshDevice()
            }
        }
    }

    private val linkDeviceResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val items: ArrayList<PickerEntity>? = result.data?.getParcelableArrayListExtra(PickerActivity.Companion.EXTRA_SELECTED_ITEMS)
                if (items?.isNotEmpty() == true) {
                    vm.linkDevices(items.map { pickerEntity ->
                        gson.fromJson(pickerEntity.dataJson, DeviceEntity::class.java).deviceId
                    }.toList(), userIdToAssign!!, object : ResponseCallback {
                        override fun onResponse(any: Any, errorMessage: String?) {
                            if (errorMessage != null) {
                                AlertHelper.showAlertDialog(requireActivity(), title = getString(R.string.error), message = errorMessage)
                            } else {
                                userIdToAssign = null
                                vm.getAllDevices()
                                vm.getAllMyUsers()
                                AlertHelper.showSnackbar(b.root, getString(R.string.linked_success))
                            }
                        }
                    })
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.extendedFAB.setOnClickListener {
            addUserForResultLauncher.launch(Intent(context, AddUserActivity::class.java))
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

        b.recyclerView.layoutManager = LinearLayoutManager(activity)
        b.recyclerView.adapter = userAdapter

        b.swipeRefresh.setOnRefreshListener {
            vm.getAllMyUsers()
        }

//        b.etSearch.addTextChangedListener {
//            userAdapter.filter(it.toString())
//        }

        actViewModel.userUpdate.observe(viewLifecycleOwner) {
            vm.getAllMyUsers()
            if (vm.allDevices.value is ResponseWrapper.Success) {
                vm.getAllDevices()
            }
        }

        vm.allUsersList.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    userAdapter.submitOriginalList(it.data)
                    b.loader.root.hide()
                    b.errorView.root.hide()
                    b.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Error -> {
                    b.loader.root.hide()
                    b.errorView.tvError.text = it.message
                    b.errorView.root.show()
                    b.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Loading -> {
                    b.loader.root.show()
                    b.errorView.root.hide()
                }
            }
        }

        vm.allDevices.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    if (userIdToAssign != null)
                        openPicker()
                }

                is ResponseWrapper.Error -> {
                    AlertHelper.showToast(requireContext(), it.message)
                }

                is ResponseWrapper.Loading -> {
                }
            }
        }
    }

    private fun createUserAdapter(): UserAdapter {
        return UserAdapter(object : RecyclerViewItemClickListener<UserEntity> {
            override fun onItemClick(viewId: Int, position: Int, data: UserEntity) {
                when (viewId) {
                    R.id.btnEdit -> {
                        userDetailResultLauncher.launch(Intent(context, AddUserActivity::class.java).putExtra("user", gson.toJson(data)))
                    }

                    R.id.btnDelete -> {
                        AlertHelper.showAlertDialog(requireContext(), title = getString(R.string.delete_user) + " ??",
                            message = getString(R.string.are_you_sure_you_want_to_delete_this_user),
                            positiveButtonText = getString(com.powersoft.common.R.string.delete),
                            negativeButtonText = getString(com.powersoft.common.R.string.cancle), onPositiveButtonClick = {
                                vm.deleteUser(data.id, object : ResponseCallback {
                                    override fun onResponse(any: Any, errorMessage: String?) {
                                        if (errorMessage != null) {
                                            AlertHelper.showAlertDialog(requireActivity(), getString(R.string.error), errorMessage)
                                        } else {
                                            userAdapter.removeItem(position)
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                if (userAdapter.currentList.isEmpty()) {
                                                    b.errorView.tvError.text = getString(R.string.no_users)
                                                    b.errorView.root.show()
                                                }
                                            }, 300)
                                        }
                                    }
                                })
                            })
                    }

                    R.id.btnAssign -> {
                        userIdToAssign = data.id
                        if (vm.allDevices.value is ResponseWrapper.Success) {
                            openPicker()
                        } else {
                            vm.getAllDevices()
                        }
                    }

                    else -> {
                        userDetailResultLauncher.launch(Intent(context, UserDetailActivity::class.java).putExtra("user", gson.toJson(data)))
                    }
                }
            }
        })
    }

    override fun onSearch(query: String) {
        userAdapter.filter(query)
    }

    override fun onItemClick(viewId: Int, position: Int, data: UserEntity) {
        userDetailResultLauncher.launch(Intent(context, UserDetailActivity::class.java).putExtra("user", gson.toJson(data)))
    }

    private fun openPicker() {
        PickerActivity.Companion.startForResult(
            requireActivity(),
            (vm.allDevices.value as ResponseWrapper.Success).data
                .mapNotNull {
                    if (it.state == State.AVAILABLE)
                        PickerEntity(it.deviceName, gson.toJson(it))
                    else
                        null
                }, true, linkDeviceResultLauncher
        )
    }
}