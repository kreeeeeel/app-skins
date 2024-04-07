package com.project.app.webdriver

import com.project.app.property.ConfigProperty
import com.project.app.property.type.BrowserType
import io.github.bonigarcia.wdm.managers.ChromeDriverManager
import io.github.bonigarcia.wdm.managers.EdgeDriverManager
import io.github.bonigarcia.wdm.managers.FirefoxDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

class BrowseDriver {

    fun getDriver(): WebDriver {

        val config = ConfigProperty()

        val manager = when(config.browser) {
            BrowserType.FIREFOX -> FirefoxDriverManager()
            BrowserType.GOOGLE -> ChromeDriverManager()
            else -> EdgeDriverManager()
        }

        val driver = when(config.browser) {
            BrowserType.FIREFOX -> FirefoxDriver( FirefoxOptions() )
            BrowserType.GOOGLE -> ChromeDriver( ChromeOptions() )
            else -> EdgeDriver( EdgeOptions() )
        }

        manager.setup()
        return driver
    }

}