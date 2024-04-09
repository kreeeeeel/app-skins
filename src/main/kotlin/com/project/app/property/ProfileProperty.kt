package com.project.app.property

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProfileProperty(
    @SerializedName("name") var name: String,
    @SerializedName("avatar") var avatar: String,
    @SerializedName("frame") var frame: String?,
    @SerializedName("trade_link") var tradeLink: String,
    @SerializedName("last_updated") var lastUpdated: Long = System.currentTimeMillis(),
    @SerializedName("steam") var steam: SteamProperty? = null,
    @SerializedName("inventory") var inventory: InventoryProperties = InventoryProperties()
)

data class InventoryProperties(
    val summa: BigDecimal = BigDecimal.ZERO
)
