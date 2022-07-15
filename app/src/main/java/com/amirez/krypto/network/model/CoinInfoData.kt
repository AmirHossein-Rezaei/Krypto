package com.amirez.krypto.network.model


import com.google.gson.annotations.SerializedName

class CoinInfoData : ArrayList<CoinInfoData.CoinInfoDataItem>(){
    data class CoinInfoDataItem(
        @SerializedName("currencyName")
        val currencyName: String,
        @SerializedName("info")
        val info: Info
    ) {
        data class Info(
            @SerializedName("desc")
            val desc: String,
            @SerializedName("forum")
            val forum: String?,
            @SerializedName("github")
            val github: String?,
            @SerializedName("reddit")
            val reddit: String?,
            @SerializedName("twt")
            val twt: String?,
            @SerializedName("web")
            val web: String?
        )
    }
}