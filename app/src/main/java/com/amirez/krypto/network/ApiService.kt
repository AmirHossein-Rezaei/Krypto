package com.amirez.krypto.network

import com.amirez.krypto.API_KEY
import com.amirez.krypto.network.model.CoinsData
import com.amirez.krypto.network.model.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder: String = "popular"
    ): Call<NewsData>

    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query("tsym") toSymbol: String = "USD",
        @Query("limit") dataLimit: Int = 10
    ): Call<CoinsData>
}