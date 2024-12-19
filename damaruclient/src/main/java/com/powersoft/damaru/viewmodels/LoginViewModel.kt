package com.powersoft.damaru.viewmodels

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.powersoft.damaru.BuildConfig
import com.powersoft.damaru.R
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(@ApplicationContext val context: Context, private val repo: AuthRepo) : BaseViewModel() {

    fun login(email: String, password: String, navigate: () -> Unit) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, context.getString(R.string.please_enter_valid_email), Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show()
        } else {
            showLoader()
            viewModelScope.launch {
                repo.loginTask(email, password)
//                hideLoader()
//                navigate()
            }
        }
    }

}