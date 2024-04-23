package com.project.app.client.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.io.FileReader
import java.util.*

class ClientInterceptor(
    private val cookie: String? = null
): Interceptor {

    val fileAgents = File("user-agents.txt")

    override fun intercept(chain: Interceptor.Chain): Response {

        if (fileAgents.exists()) {
            val userAgents = FileReader(fileAgents).use { it.readLines() }
            val request = chain.request().newBuilder().also {
                it.addHeader("User-Agent", getRandomUserAgent(userAgents))

                if (cookie != null) {
                    it.addHeader("Cookie", cookie)
                }
            }.build()

            return chain.proceed(request)
        }

        return chain.proceed(chain.request())
    }

    private fun getRandomUserAgent(userAgents: List<String>): String {
        val randomIndex = Random().nextInt(userAgents.size)
        return userAgents[randomIndex]
    }
}