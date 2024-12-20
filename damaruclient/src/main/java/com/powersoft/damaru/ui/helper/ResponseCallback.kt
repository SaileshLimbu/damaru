package com.powersoft.damaru.ui.helper

import com.powersoft.damaru.models.ErrorResponse

interface ResponseCallback {
    fun onResponse(any: Any, errorResponse: ErrorResponse? = null)
}