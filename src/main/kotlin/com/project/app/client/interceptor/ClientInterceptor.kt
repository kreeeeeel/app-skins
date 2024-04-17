package com.project.app.client.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.FileReader
import java.util.*

class ClientInterceptor(
    private val cookie: String? = null
): Interceptor {

    private val userAgents = FileReader("user-agents.txt")
        .use { it.readLines() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().also {
            it.addHeader("User-Agent", getRandomUserAgent())

            if (cookie != null) {
                it.addHeader("Cookie", cookie)
            }
        }.build()

        return chain.proceed(request)
    }

    private fun getRandomUserAgent(): String {
        val randomIndex = Random().nextInt(userAgents.size)
        return userAgents[randomIndex]
    }
}