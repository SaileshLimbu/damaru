package com.powersoft.damaruadmin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.model.ErrorResponse
import com.powersoft.common.model.ResponseWrapper
import com.powersoft.common.model.UserEntity
import com.powersoft.common.model.getUnknownError
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val apiService: ApiServiceImpl,
    private val gson: Gson
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
                if (response.isSuccessful) {
                    _allUsersList.postValue(response.body()?.let {
                        ResponseWrapper.success(it)
                    } ?: ResponseWrapper.error(getUnknownError("Something went wrong (Code 342)")))
                } else {
                    val errorResponse = try {
                        val errorBody = response.errorBody()?.string()
                        val error = gson.fromJson(errorBody, ErrorResponse::class.java)
                        error
                    } catch (e: Exception) {
                        getUnknownError()
                    }
                    _allUsersList.postValue(ResponseWrapper.error(errorResponse))
                }
            } catch (e: Exception) {
                //catch
            }
        }
    }

}