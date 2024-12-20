package com.powersoft.damaru.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.models.AccountsEntity
import com.powersoft.damaru.models.ResponseWrapper
import com.powersoft.damaru.repository.DeviceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountUsersViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repo: DeviceRepo,
) : BaseViewModel() {

    private var _mutableLiveData = MutableLiveData<ResponseWrapper<AccountsEntity>>()

    val liveData: LiveData<ResponseWrapper<AccountsEntity>>
        get() = _mutableLiveData

    init {
        _mutableLiveData.value = ResponseWrapper.loading()
    }

    fun getAccountUsers() {
        showLoader()
        viewModelScope.launch {
            _mutableLiveData.value = repo.accountsListTask()
            hideLoader()
        }
    }
}