package com.project.app.client.response

import com.google.gson.annotations.SerializedName

data class RefreshResponse(
    @SerializedName("response") val response: RefreshTokenResponse,
)

data class RefreshTokenResponse(
    @SerializedName("refresh_token") val refreshToken: String,
)
