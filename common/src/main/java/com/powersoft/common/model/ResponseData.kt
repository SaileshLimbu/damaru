package com.powersoft.common.model

data class ResponseData<T>(val status: Boolean, val message: String, val data: T?)