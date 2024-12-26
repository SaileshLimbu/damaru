package com.powersoft.damaruadmin.viewmodels

import android.content.Context
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.ui.helper.AlertHelper
import com.powersoft.damaruadmin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewModel() {
    fun addUser(name: String, email: String, password: String, confirmPassword: String) {
        if(name.isEmpty()){
            AlertHelper.showToast(context, context.getString(R.string.please_enter_name))
        }else if(email.isEmpty()){

        }
    }
}