package com.amirez.krypto.network

import com.amirez.krypto.*
import com.amirez.krypto.network.model.ChartData
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
        apiService.getTopCoins().enqueue(object : Callback<CoinsData> {
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                val data = response.body()
                callback.onSuccess(data!!.data)
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                callback.onFailure(t.message!!)
            }
        })
    }

    fun getChartData(
        period: String,
        symbol: String,
        callback: ApiCallback<Pair<List<ChartData.Data.Data>, ChartData.Data.Data?>>
    ) {
        val chartQueryParameters = calculateAndGetChartQueryParameters(period)
        val periodHistory = chartQueryParameters.first
        val limit = chartQueryParameters.second
        val aggregate = chartQueryParameters.third

        apiService.getChartData(periodHistory, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {
                    val chartData = response.body()!!.data.data
                    val chartBaseLine =
                        response.body()!!.data.data.maxByOrNull { it.close.toFloat() }
                    val pair = Pair(chartData, chartBaseLine)
                    callback.onSuccess(pair)
                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    callback.onFailure(t.message!!)
                }
            })
    }

    private fun calculateAndGetChartQueryParameters(period: String): Triple<String, Int, Int> {
        val periodHistory: String

        //defaults
        var limit = 30
        var aggregate = 1

        when (period) {

            HOUR12 -> {
                periodHistory = MINUTE_HISTORY
                limit = 24
                aggregate = 30
            }

            DAY -> {
                periodHistory = HOUR_HISTORY
                limit = 24
            }

            WEEK -> {
                periodHistory = HOUR_HISTORY
                limit = 28
                aggregate = 6
            }

            MONTH -> {
                periodHistory = DAY_HISTORY
                limit = 30
            }

            MONTH3 -> {
                periodHistory = DAY_HISTORY
                limit = 90
                //aggregate = 6
            }

            YEAR -> {
                periodHistory = DAY_HISTORY
                aggregate = 12
            }

            ALL -> {
                periodHistory = DAY_HISTORY
                aggregate = 30
                limit = 2000
            }

            else -> periodHistory = "" //not going to happen
        }

        return Triple(periodHistory, limit, aggregate)
    }
}