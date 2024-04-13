package com.project.app.service.steam.impl

import com.project.app.client.api.Profile
import com.project.app.client.interceptor.RandomUserAgentInterceptor
import com.project.app.client.response.SteamProfileResponse
import com.project.app.service.steam.SteamProfile
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Suppress("deprecation")
class DefaultSteamProfile: SteamProfile {

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(RandomUserAgentInterceptor())
        .build()

    private val clientXml = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/")
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .client(httpClient)
        .build()

    private val client = Retrofit.Builder()
        .baseUrl("https://steamcommunity.com/")
        //.addConverterFactory(ScalarsConverterFactory.create())
        //.client(httpClient)
        .build()

    private val apiXml = clientXml.create(Profile::class.java)
    private val api = client.create(Profile::class.java)

    override fun getProfileData(steamId: String): SteamProfileResponse? =
        apiXml.getProfile(steamId).execute().body()

    override fun getTradeLink(steamId: String, cookie: String): String? {
        val execute = api.getTradeLink(steamId, cookie).execute()
        val html = execute.body()?.string() ?: return null

        val document = Jsoup.parse(html)
        val element = document.getElementById("trade_offer_access_url") ?: return null

        return element.`val`()
    }

}