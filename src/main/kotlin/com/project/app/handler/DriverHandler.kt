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

            val lisSkinHandler = LisSkinHandler(webDriver, webDriverWait)
            lisSkinHandler.auth(profileProperty.tradeLink)

            val inventory = lisSkinHandler.getInventory()
            profileProperty.inventory = inventory

            val profileRepository = ProfileRepository()
            profileRepository.save(profileProperty)

            return true
        } catch (e: Exception) {
            return false
        } finally {
            webDriver.quit()
        }
    }

    fun startTask() {
        val steamAuthHandler = SteamAuthHandler(webDriver, webDriverWait)
        val lisSkinHandler = LisSkinHandler(webDriver, webDriverWait)
        val profileRepository = ProfileRepository()

        for (profile in profileRepository.findAll()) {
            try {
                steamAuthHandler.auth(profile)
                lisSkinHandler.auth()
                lisSkinHandler.getInventory()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                val currentWindowHandle = webDriver.windowHandles.elementAt(0)
                webDriver.windowHandles.forEach { window ->
                    if (window != currentWindowHandle) {
                        webDriver.switchTo().window(window)
                        webDriver.close()
                    }
                }
                webDriver.switchTo().window(currentWindowHandle)
                webDriver.manage().deleteAllCookies()
            }
        }
        webDriver.quit()
    }


}