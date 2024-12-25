package com.powersoft.common.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.powersoft.common.BuildConfig
import com.powersoft.common.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    userRepo: UserRepo
) : ViewModel() {

    private val _navigateToMain = MutableLiveData<String>()
    val navigateToMain: LiveData<String> get() = _navigateToMain

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!userRepo.userEntity.value?.accessToken.isNullOrEmpty()) {
                if(userRepo.userEntity.value?.firstLogin == true){
                    _navigateToMain.value = "resetPin"
                }else {
                    _navigateToMain.value = "dashboard"
                }
            } else {
                _navigateToMain.value = "login"
            }
        }, if (BuildConfig.DEBUG) 1 else 3000)
    }
}
