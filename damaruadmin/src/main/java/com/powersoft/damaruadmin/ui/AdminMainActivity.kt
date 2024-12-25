package com.powersoft.damaruadmin.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.common.utils.Logg
import com.powersoft.damaruadmin.adapters.UserAdapter
import com.powersoft.damaruadmin.databinding.ActivityAdminMainBinding
import com.powersoft.damaruadmin.viewmodels.AdminMainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminMainActivity : BaseActivity() {
    private lateinit var binding: ActivityAdminMainBinding
    private val viewModel: AdminMainViewModel by viewModels()

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.extendedFAB.setOnClickListener {

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allUsersList.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    binding.recyclerView.adapter = UserAdapter(it.data,
                        object : RecyclerViewItemClickListener<UserEntity> {
                            override fun onItemClick(position: Int, data: UserEntity) {
                            }
                        })
                    binding.loader.root.visibility = View.GONE
                    binding.errorView.root.visibility = View.GONE
                    Logg.d("FUck this")
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.visibility = View.GONE
                    binding.errorView.tvError.text = it.errorResponse.message?.message
                    binding.errorView.root.visibility = View.VISIBLE
                    Logg.d("FUck that")
                }

                is ResponseWrapper.Loading -> {
                    binding.loader.root.visibility = View.VISIBLE
                    binding.errorView.root.visibility = View.GONE
                    Logg.d("FUck it")
                }
            }
        }
    }


}