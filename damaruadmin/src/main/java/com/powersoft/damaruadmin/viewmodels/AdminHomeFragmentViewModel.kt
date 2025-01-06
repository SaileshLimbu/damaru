package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.damaruadmin.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminHomeFragmentViewModel @Inject constructor(
    private val userRepo: UserRepo
) : BaseViewModel() {

    private val _allUsersList = MutableLiveData<ResponseWrapper<List<UserEntity>>>()

    val allUsersList: LiveData<ResponseWrapper<List<UserEntity>>>
        get() = _allUsersList

    init {
        getALlMyUsers()
    }

    fun getALlMyUsers() {
        viewModelScope.launch {
            val response = userRepo.getAllUsers()
            _allUsersList.postValue(response)
        }
    }

    fun deleteUser(id: String, responseCallback: ResponseCallback) {
        viewModelScope.launch {
            when (val response = userRepo.deleteUserTask(id)) {
                is ResponseWrapper.Success -> {
                    responseCallback.onResponse(response.data, null)
                }

                is ResponseWrapper.Error -> {
                    responseCallback.onResponse(Any(), response.message)
                }

                is ResponseWrapper.Loading -> {
                }
            }
        }
    }
}