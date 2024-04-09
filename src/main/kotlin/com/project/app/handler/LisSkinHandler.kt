package com.project.app.handler

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

private const val LIS_SKIN_LINK = "https://lis-skins.ru/"

private val xPathAuthButton: By = By.xpath("//a[@class='login-button']")
private val xPathSteamAuthButton: By = By.xpath("//input[@class='btn_green_white_innerfade']")

class LisSkinHandler(
    private val webDriver: WebDriver,
    private val webDriverWait: WebDriverWait,
) {

    fun auth() {
        webDriver.get(LIS_SKIN_LINK)

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathAuthButton)).click()
        webDriver.switchTo().window(webDriver.windowHandles.elementAt(webDriver.windowHandles.size - 1))

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathSteamAuthButton)).click()
        webDriver.switchTo().window(webDriver.windowHandles.elementAt(webDriver.windowHandles.size - 1))
    }

}