package com.project.app.property

import com.google.gson.annotations.SerializedName

data class ProfileProperty(
    @SerializedName("name") var name: String,
    @SerializedName("avatar") var avatar: String,
    @SerializedName("frame") var frame: String?,
    @SerializedName("level") var level: Int,
    @SerializedName("trade_link") var tradeLink: String,
    @SerializedName("last_updated") var lastUpdated: Long,
    @SerializedName("steam") var steam: SteamProperty
)
