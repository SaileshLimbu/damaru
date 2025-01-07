package com.powersoft.common.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.powersoft.common.adapter.LogsAdapter
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.databinding.ActivityLogsBinding
import com.powersoft.common.listeners.RecyclerViewItemClickListener
import com.powersoft.common.model.LogsEntity
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.utils.hide
import com.powersoft.common.utils.show
import com.powersoft.common.viewmodels.LogsActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogsActivity @Inject constructor(): BaseActivity() {
    private val viewModel: LogsActivityViewModel by viewModels()
    lateinit var binding: ActivityLogsBinding
    private val logsAdapter by lazy { createLogAdapter() }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = logsAdapter

        viewModel.allLogs.observe(this) {
            when (it) {
                is ResponseWrapper.Success -> {
                    logsAdapter.submitList(it.data)
                    binding.loader.root.hide()
                    binding.errorView.root.hide()

                    binding.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Error -> {
                    binding.loader.root.hide()
                    binding.errorView.tvError.text = it.message
                    binding.errorView.root.show()

                    binding.swipeRefresh.isRefreshing = false
                }

                is ResponseWrapper.Loading -> {
                    binding.loader.root.show()
                    binding.errorView.root.hide()
                }
            }
        }

    }

    private fun createLogAdapter(): LogsAdapter {
        return LogsAdapter(object : RecyclerViewItemClickListener<LogsEntity> {
            override fun onItemClick(viewId: Int, position: Int, data: LogsEntity) {
            }
        })
    }
}