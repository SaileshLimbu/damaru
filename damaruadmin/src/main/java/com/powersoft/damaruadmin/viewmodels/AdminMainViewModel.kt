package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val apiService: ApiServiceImpl
) : BaseViewModel() {

    private val _allUsersList = MutableLiveData<ResponseWrapper<List<UserEntity>>>()

    val allUsersList: LiveData<ResponseWrapper<List<UserEntity>>>
        get() = _allUsersList

    init {
        getALlMyUsers()
    }

    private fun getALlMyUsers() {
        viewModelScope.launch {
            try {
                val response = apiService.getAllUsers()
            }catch (e: Exception){

            }
        }
    }

}