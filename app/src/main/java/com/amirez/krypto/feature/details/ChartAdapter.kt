package com.amirez.krypto.feature.details

import com.amirez.krypto.network.model.ChartData
import com.robinhood.spark.SparkAdapter

class ChartAdapter(
    private val data: List<ChartData.Data.Data>,
    private val baseLine: String?
) : SparkAdapter() {

    override fun getCount(): Int = data.size

    override fun getItem(index: Int): ChartData.Data.Data = data[index]

    override fun getY(index: Int): Float = data[index].close.toFloat()

    override fun hasBaseLine(): Boolean = true

    override fun getBaseLine(): Float = baseLine?.toFloat() ?: super.getBaseLine()
}