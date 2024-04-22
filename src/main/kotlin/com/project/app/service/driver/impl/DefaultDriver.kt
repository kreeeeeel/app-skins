package com.project.app.service.driver.impl

import com.project.app.data.type.BrowserType
import com.project.app.models.ConfigModel
import com.project.app.service.driver.Driver
import com.project.app.service.logger.Logger
import com.project.app.service.logger.impl.DefaultLogger
import io.github.bonigarcia.wdm.managers.ChromeDriverManager
import io.github.bonigarcia.wdm.managers.EdgeDriverManager
import io.github.bonigarcia.wdm.managers.FirefoxDriverManager
import org.openqa.selenium.Cookie
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver

class DefaultDriver: Driver {

    private val config = ConfigModel().init()
    private val logger: Logger = DefaultLogger()

    private val webDriverManager = when(config.browserType) {
        BrowserType.FIREFOX -> FirefoxDriverManager()
        BrowserType.GOOGLE -> ChromeDriverManager()
        else -> EdgeDriverManager()
    }

    private val webDriver: WebDriver = when(config.browserType) {
        BrowserType.FIREFOX -> FirefoxDriver()
        BrowserType.GOOGLE -> ChromeDriver()
        else -> EdgeDriver()
    }

    init {
        logger.info("Установка драйвера Selenium: ${getBrowserName()}")
        webDriverManager.setup()
    }

    override fun openBrowseProfile(name: String, value: String) {
        logger.info("Запуска драйвера Selenium: ${getBrowserName()}")
        webDriver.get("https://steamcommunity.com")
        logger.debug("${getBrowserName()}: Переход по ссылке: \"https://steamcommunity.com\"")
        webDriver.manage().addCookie(Cookie(name, value))
        logger.debug("${getBrowserName()}: Добавление Cookie в браузер")
        webDriver.get("https://steamcommunity.com")
        logger.debug("${getBrowserName()}: Обновление страницы Steam")
    }

    private fun getBrowserName(): String = when(config.browserType) {
        BrowserType.FIREFOX -> "Mozilla Firefox"
        BrowserType.GOOGLE -> "Google Chrome"
        else -> "Microsoft Edge"
    }
}