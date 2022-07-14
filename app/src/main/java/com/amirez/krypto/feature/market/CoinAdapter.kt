package com.amirez.krypto.feature.market

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amirez.krypto.databinding.ItemCoinBinding
import com.amirez.krypto.BASE_URL_IMAGE
import com.amirez.krypto.ONE_BILLION
import com.amirez.krypto.R
import com.amirez.krypto.network.model.CoinsData
import com.bumptech.glide.Glide

class CoinAdapter(private val data: ArrayList<CoinsData.Data>, private val coinEvent: CoinEvent) :
    RecyclerView.Adapter<CoinAdapter.CoinViewHolder>() {
    private lateinit var binding: ItemCoinBinding

    inner class CoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindViews(coin: CoinsData.Data) {
            binding.tvCurrencyName.text = coin.coinInfo.fullName
            binding.tvPrice.text = "$" + coin.rAW.uSD.pRICE.toString()

            // set and changes the text color depending on the price change
            val change = coin.rAW.uSD.cHANGEPCTHOUR
            setTextForTVChange(change)

            var marketCap = (coin.rAW.uSD.mKTCAP / ONE_BILLION).toString()
            marketCap = marketCap.substring(0, marketCap.indexOf('.') + 3)
            binding.tvMarketCap.text = "$$marketCap B"

            // set the icon
            Glide.with(itemView)
                .load(BASE_URL_IMAGE + coin.coinInfo.imageUrl)
                .into(binding.imgCurrencyIcon)

            itemView.setOnClickListener { coinEvent.onCoinClick(coin) }
        }

        private fun setTextForTVChange(change: Double) {
            when {
                change > 0 -> {
                    val text = change.toString().substring(0, 4) + "%"
                    binding.tvChange.text = text
                    binding.tvChange.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_gain
                        )
                    )
                }
                change < 0 -> {
                    val text = change.toString().substring(0, 5) + "%"
                    binding.tvChange.text = text
                    binding.tvChange.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.color_loss
                        )
                    )
                }
                else -> {
                    binding.tvChange.text = "$change%"
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        binding = ItemCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CoinViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        val coin = data[position]
        holder.bindViews(coin)

    }

    override fun getItemCount(): Int = data.size

    interface CoinEvent {
        fun onCoinClick(coinData: CoinsData.Data)
    }
}