package com.amirez.krypto.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinInfoItem(
    val website: String? = "N/A",
    val github: String? = "N/A",
    val twitter: String? = "N/A",
    val desc: String? = "N/A",
    val reddit: String? = "N/A"
): Parcelable
