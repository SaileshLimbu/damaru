package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel

import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity

import com.powersoft.common.utils.PrefsHelper
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val apiService: ApiServiceImpl,
    private val gson: Gson,
    private val prefsHelper: PrefsHelper
) : BaseViewModel() {

    private val _allUsersList = MutableLiveData<ResponseWrapper<List<UserEntity>>>()

    val allUsersList: LiveData<ResponseWrapper<List<UserEntity>>>
        get() = _allUsersList

    init {
        getALlMyUsers()
    }

    fun logout(){
        prefsHelper.clear()
    }

    fun getALlMyUsers() {
        viewModelScope.launch {
            try {
                val response = apiService.getAllUsers()
                if (response.status) {
                    if(response.data.isNullOrEmpty()){
                        _allUsersList.postValue(ResponseWrapper.error("No users found"))
                    }else {
                        _allUsersList.postValue(ResponseWrapper.success(response.data!!))
                    }
                } else {
                    _allUsersList.postValue(ResponseWrapper.error(response.message))
                }
            } catch (e: Exception) {
                _allUsersList.postValue(ResponseWrapper.error("Something went wrong. Code(342)"))
            }
        }
    }

}