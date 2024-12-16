package com.powersoft.damaru.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(@ApplicationContext val context: Context) : ViewModel() {
    fun login(username: String, password: String, navigate : () -> Unit) {
        if(username.isEmpty()){
            Toast.makeText(context, "Please enter username", Toast.LENGTH_SHORT).show()
        }else if(password.isEmpty()){
            Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
        }else{
            navigate()
        }
    }

}