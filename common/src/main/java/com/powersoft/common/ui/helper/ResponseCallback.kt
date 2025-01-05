package com.powersoft.common.ui.helper

interface ResponseCallback {
    fun onResponse(any: Any, errorMessage: String? = null)
}