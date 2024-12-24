package com.powersoft.common.ui.helper

import com.powersoft.common.model.ErrorResponse


interface ResponseCallback {
    fun onResponse(any: Any, errorResponse: ErrorResponse? = null)
}