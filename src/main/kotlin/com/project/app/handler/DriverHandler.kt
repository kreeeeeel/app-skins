package com.project.app.handler

import com.project.app.property.SteamProperty
import com.project.app.repository.ProfileRepository
import com.project.app.webdriver.BrowseDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class DriverHandler {

    private val webDriver = BrowseDriver().getDriver()
    private val webDriverWait = WebDriverWait(webDriver, Duration.ofSeconds(10))

    fun auth(steamProperty: SteamProperty, password: String): Boolean {
        try {
            val authHandler = SteamAuthHandler(webDriver, webDriverWait)

            val profileProperty = authHandler.getProfileProperty(steamProperty, password)
            val profileRepository = ProfileRepository()
            profileRepository.save(profileProperty)

            val lisSkinHandler = LisSkinHandler(webDriver, webDriverWait)
            lisSkinHandler.auth()

            return true
        } catch (e: Exception) {
            println(e.message)
            return false
        } finally {
            webDriver.quit()
        }
    }

}