package com.powersoft.damaru.models

sealed class ResponseWrapper<T> {
    data class Success<T>(val data: T) : ResponseWrapper<T>()
    data class Error<T>(val errorResponse: ErrorResponse) : ResponseWrapper<T>()
    class Loading<T> : ResponseWrapper<T>()

    companion object {
        fun <T> success(data: T): ResponseWrapper<T> = Success(data)
        fun <T> error(errorResponse: ErrorResponse): ResponseWrapper<T> = Error(errorResponse)
        fun <T> loading(): ResponseWrapper<T> = Loading()
    }
}
