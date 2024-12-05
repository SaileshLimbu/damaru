package com.d1vivek.projectz.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.d1vivek.projectz.BuildConfig
import com.d1vivek.projectz.utils.PrefsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    prefsHelper: PrefsHelper
) : ViewModel() {

    private val _navigateToMain = MutableLiveData<String>()
    val navigateToMain: LiveData<String> get() = _navigateToMain

    init {
        Handler(Looper.getMainLooper()).postDelayed({
            if (BuildConfig.emulatorBuild) {
                val emulatorId = prefsHelper.getString(PrefsHelper.EMULATOR_ID)
                if (emulatorId.isNullOrEmpty()) {
                    _navigateToMain.value = "emulatorSetup"
                } else {
                    _navigateToMain.value = "emulator"
                }
            } else {
                val isLoggedIn = prefsHelper.getBoolean(PrefsHelper.LOGGED_IN)
                if (isLoggedIn) {
                    _navigateToMain.value = "userPin"
                } else {
                    _navigateToMain.value = "login"
                }
            }
        }, if (BuildConfig.DEBUG) 1 else 3000)
    }
}
