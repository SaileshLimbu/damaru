package com.powersoft.common.model

sealed class ResponseWrapper<T> {
    data class Success<T>(val data: T) : ResponseWrapper<T>()
    data class Error<T>(val message: String) : ResponseWrapper<T>()
    class Loading<T> : ResponseWrapper<T>()

    companion object {
        fun <T> success(data: T): ResponseWrapper<T> = Success(data)
        fun <T> error(message: String): ResponseWrapper<T> = Error(message)
        fun <T> loading(): ResponseWrapper<T> = Loading()
    }
}
