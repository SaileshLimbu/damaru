package com.powersoft.damaru.viewmodels

import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.repository.AccountsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePinViewModel @Inject constructor(
    private val accountRepo: AccountsRepo
) : BaseViewModel() {

}