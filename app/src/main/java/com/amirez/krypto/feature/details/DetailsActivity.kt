package com.amirez.krypto.feature.details

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amirez.krypto.*
import com.amirez.krypto.databinding.ActivityDetailsBinding
import com.amirez.krypto.network.ApiCallback
import com.amirez.krypto.network.ApiManager
import com.amirez.krypto.network.model.ChartData
import com.amirez.krypto.network.model.CoinInfoItem
import com.amirez.krypto.network.model.CoinsData

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var coinData: CoinsData.Data
    private lateinit var coinInfo: CoinInfoItem
    private lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiManager = ApiManager()

        coinData = intent.getParcelableExtra(COIN_DATA_KEY)!!
        coinInfo = intent.getParcelableExtra(COIN_INFO_KEY)!!


        binding.layoutToolbar.toolbarMain.title = coinData.coinInfo.fullName

        binding.swipeDetails.setOnRefreshListener {
            refreshUI()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeDetails.isRefreshing = false
            }, 1500L)
        }
    }

    override fun onResume() {
        super.onResume()
        initializeUI()
    }

    private fun refreshUI() {
        initChart()
        initStatistics()
    }

    private fun initializeUI() {
        initChart()
        initAbout()
        initStatistics()
    }

    private fun initStatistics() {
        binding.layoutStatistics.tvOpenAmount.text = coinData.dISPLAY.uSD.oPEN24HOUR
        binding.layoutStatistics.tvHighestAmount.text = coinData.dISPLAY.uSD.hIGH24HOUR
        binding.layoutStatistics.tvLowestAmount.text = coinData.dISPLAY.uSD.lOW24HOUR
        binding.layoutStatistics.tvAlgorithm.text = coinData.coinInfo.algorithm
        binding.layoutStatistics.tvChangeAmount.text = coinData.dISPLAY.uSD.cHANGE24HOUR
        binding.layoutStatistics.tvMarketCapAmount.text = coinData.dISPLAY.uSD.mKTCAP
        binding.layoutStatistics.tvSupplyAmount.text = coinData.dISPLAY.uSD.sUPPLY
        binding.layoutStatistics.tvTotalVolumeAmount.text = coinData.dISPLAY.uSD.tOTALVOLUME24H
    }

    private fun initAbout() {

        if (coinInfo.website != NA) {
            binding.layoutAbout.tvWebsiteLink.text = coinInfo.website
            binding.layoutAbout.tvWebsiteLink.setOnClickListener { openLinkInBrowser(coinInfo.website!!) }
        }

        if (coinInfo.twitter != NA) {
            binding.layoutAbout.tvTwitterLink.text = TWITTER_BASE_URL + coinInfo.twitter
            binding.layoutAbout.tvTwitterLink.setOnClickListener {
                openLinkInBrowser(
                    TWITTER_BASE_URL + coinInfo.twitter!!
                )
            }
        }

        if (coinInfo.reddit != NA) {
            binding.layoutAbout.tvRedditLink.text = coinInfo.reddit
            binding.layoutAbout.tvRedditLink.setOnClickListener { openLinkInBrowser(coinInfo.reddit!!) }
        }

        if (coinInfo.github != NA) {
            binding.layoutAbout.tvGithubLink.text = coinInfo.github
            binding.layoutAbout.tvGithubLink.setOnClickListener { openLinkInBrowser(coinInfo.github!!) }
        }

        if (coinInfo.desc != NA)
            binding.layoutAbout.tvMoreInfo.text = coinInfo.desc

    }

    private fun openLinkInBrowser(url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
            startActivity(it)
        }
    }

    private fun initChart() {
        showChartData()
        showPriceChangesInChartLayout()
        setOnScrubTextChange()
        binding.layoutChart.rgPeriodsChart.setOnCheckedChangeListener { _, _ ->
            showChartData()
        }
    }

    private fun showPriceChangesInChartLayout() {
        binding.layoutChart.tvPriceChart.text = coinData.dISPLAY.uSD.pRICE
        binding.layoutChart.tvPriceChangeChart.text = coinData.dISPLAY.uSD.cHANGE24HOUR
        setTextColorForTVChangeAndChartLine(coinData.rAW.uSD.cHANGEPCT24HOUR)
    }

    private fun setOnScrubTextChange() {
        binding.layoutChart.sparkView.setScrubListener {
            if(it != null) {
                binding.layoutChart.tvPriceChart.text ="$ " + (it as ChartData.Data.Data).close.toString()
            } else
                binding.layoutChart.tvPriceChart.text = coinData.dISPLAY.uSD.pRICE
        }
    }

    private fun setTextColorForTVChangeAndChartLine(change: Double) {
        when {
            change > 0 -> {
                val text = change.toString().substring(0, 4) + "%"
                val green = ContextCompat.getColor(this, R.color.color_gain)

                binding.layoutChart.tvPercentChangeChart.text = text
                binding.layoutChart.tvUpOrDownChart.text = UP
                binding.layoutChart.tvPercentChangeChart.setTextColor(green)
                binding.layoutChart.tvUpOrDownChart.setTextColor(green)
                binding.layoutChart.sparkView.lineColor = green
            }
            change < 0 -> {
                val text = change.toString().substring(0, 5) + "%"
                val red = ContextCompat.getColor(this, R.color.color_loss)
                binding.layoutChart.tvPercentChangeChart.text = text
                binding.layoutChart.tvUpOrDownChart.text = DOWN
                binding.layoutChart.tvUpOrDownChart.setTextColor(red)
                binding.layoutChart.tvPercentChangeChart.setTextColor(red)
                binding.layoutChart.sparkView.lineColor = red
            }
            else -> {
                binding.layoutChart.tvPercentChangeChart.text = "$change%"
            }
        }
    }

    private fun showChartData() {
        apiManager.getChartData(
            getPeriodFromRadioButtons(),
            coinData.coinInfo.name,
            object : ApiCallback<Pair<List<ChartData.Data.Data>, ChartData.Data.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data.Data>, ChartData.Data.Data?>) {
                    var i = 1
                    data.first.forEach {
                        Log.d("tagx", "onSuccess: $i")
                        i++
                    }
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkView.adapter = chartAdapter
                    binding.layoutChart.tvNetworkError.visibility = View.GONE
                }

                override fun onFailure(errorMessage: String) {
                    binding.layoutChart.tvNetworkError.visibility = View.VISIBLE
                    Toast.makeText(this@DetailsActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getPeriodFromRadioButtons(): String {
        return when (binding.layoutChart.rgPeriodsChart.checkedRadioButtonId) {
            R.id.rb_12h -> {
                HOUR12
            }
            R.id.rb_1d -> {
                DAY
            }
            R.id.rb_1w -> {
                WEEK
            }
            R.id.rb_1m -> {
                MONTH
            }
            R.id.rb_3m -> {
                MONTH3
            }
            R.id.rb_1y -> {
                YEAR
            }
            R.id.rb_all -> {
                ALL
            }
            else -> ""
        }
    }
}