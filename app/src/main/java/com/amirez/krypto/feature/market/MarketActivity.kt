package com.amirez.krypto.feature.market

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirez.krypto.COIN_DATA_KEY
import com.amirez.krypto.LIVE_WATCH_LIST_URL
import com.amirez.krypto.databinding.ActivityMarketBinding
import com.amirez.krypto.feature.DetailsActivity
import com.amirez.krypto.network.ApiCallback
import com.amirez.krypto.network.ApiManager
import com.amirez.krypto.network.model.CoinsData

class MarketActivity : AppCompatActivity(), CoinAdapter.CoinEvent {
    private lateinit var binding: ActivityMarketBinding
    private var newsList: ArrayList<Pair<String, String>>? = null
    private val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
        binding.layoutCoins.btnMore.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse(LIVE_WATCH_LIST_URL)).also {
                startActivity(it)
            }
        }

        binding.swipeMain.setOnRefreshListener {
            initializeUI()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeMain.isRefreshing = false
            }, 1500L)
        }
    }

    private fun initializeUI() {
        getNews()
        getTopCoins()
    }

    private fun getTopCoins() {
        apiManager.getTopCoinsList(object : ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showTopCoins(data)
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@MarketActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showTopCoins(coinsData: List<CoinsData.Data>) {
        val coinAdapter = CoinAdapter(ArrayList(coinsData), this)
        binding.layoutCoins.rvCoins.layoutManager = LinearLayoutManager(this)
        binding.layoutCoins.rvCoins.adapter = coinAdapter
    }

    private fun getNews() {
        apiManager.getTopNews(object : ApiCallback<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                newsList = data
                showNews()
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "Something happened!", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showNews() {
        val randomNum = (0..48).random()
        val news = newsList!![randomNum]
        binding.layoutNews.tvNews.text = news.first

        binding.layoutNews.btnVisit.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.second))
            startActivity(intent)
        }

        binding.layoutNews.btnNextNews.setOnClickListener {
            showNews()
        }
    }

    override fun onCoinClick(coinData: CoinsData.Data) {
        Intent(this, DetailsActivity::class.java).also {
            it.putExtra(COIN_DATA_KEY, coinData)
            startActivity(it)
        }
    }
}