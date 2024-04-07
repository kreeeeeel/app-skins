package com.project.app.client.api

import com.project.app.client.response.ServerTime
import retrofit2.Call
import retrofit2.http.POST

interface SteamApi {
    @POST("ITwoFactorService/QueryTime/v0001")
    fun getServerTime(): Call<ServerTime>
}