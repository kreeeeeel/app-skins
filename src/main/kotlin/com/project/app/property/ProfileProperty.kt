package com.project.app.property

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ProfileProperty(
    @SerializedName("name") var name: String,
    @SerializedName("avatar") var avatar: String,
    @SerializedName("frame") var frame: String?,
    @SerializedName("trade_link") var tradeLink: String,
    @SerializedName("password") var password: String,
    @SerializedName("last_updated") var lastUpdated: Long = System.currentTimeMillis(),
    @SerializedName("steam") var steam: SteamProperty? = null,
    @SerializedName("inventory") var inventory: InventoryProperties? = null
)

data class InventoryProperties(
    val summa: BigDecimal = BigDecimal.ZERO,
    val items: List<ItemProperties> = emptyList()
)

data class ItemProperties(
    val assetId: String,
    val name: String,
    val image: String,
    val exterior: String,
    val price: BigDecimal
)
