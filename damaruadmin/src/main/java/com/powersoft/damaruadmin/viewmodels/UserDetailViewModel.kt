package com.powersoft.damaruadmin.viewmodels

import com.powersoft.common.base.BaseViewModel
import com.powersoft.damaruadmin.webservices.ApiServiceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val apiServiceImpl: ApiServiceImpl
) : BaseViewModel() {
}