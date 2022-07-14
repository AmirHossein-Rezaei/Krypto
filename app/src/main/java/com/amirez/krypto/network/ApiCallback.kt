package com.amirez.krypto.network

interface ApiCallback<T> {
    fun onSuccess(data: T)
    fun onFailure(errorMessage: String)
}