package com.amirez.krypto.network

import com.amirez.krypto.BASE_URL
import com.amirez.krypto.network.model.CoinsData
import com.amirez.krypto.network.model.NewsData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {
    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getTopNews(callback: ApiCallback<ArrayList<Pair<String, String>>>) {
        apiService.getTopNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                val data = response.body()!!
                val requiredData: ArrayList<Pair<String, String>> = arrayListOf()
                data.data.forEach {
                    val pair = Pair(it.title, it.url)
                    requiredData.add(pair)
                }
                callback.onSuccess(requiredData)
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                callback.onFailure(t.message!!)
            }
        })
    }

    fun getTopCoinsList(callback: ApiCallback<List<CoinsData.Data>>) {
        apiService.getTopCoins().enqueue(object : Callback<CoinsData>{
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                val data = response.body()
                callback.onSuccess(data!!.data)
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                callback.onFailure(t.message!!)
            }
        })
    }
}