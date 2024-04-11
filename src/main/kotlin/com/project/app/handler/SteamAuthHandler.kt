package com.project.app.handler

import com.project.app.property.ProfileProperty
import com.project.app.property.SteamProperty
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

private const val STEAM_AUTH_LINK = "https://steamcommunity.com/login/home/?goto="

private val xPathInput: By = By.xpath("//input[@class='_2eKVn6g5Yysx9JmutQe7WV']")
private val xPathButton: By = By.xpath("//button[@class='_2QgFEj17t677s3x299PNJQ']")
private val xPathGuard: By = By.xpath("//input[@class='HPSuAjHOkNfMHwURXTns7 Focusable']")
private val xPathGuardClick: By = By.xpath("//div[@class='_1cnUQ3RrQgqaLodJp0Ljnt _2meUB-qHRC9f-cX8Rw8QaZ']")

private val xPathName: By = By.xpath("//span[@class='actual_persona_name']")
private val xPathAvatar: By = By.xpath("//div[@class='playerAvatarAutoSizeInner']")
private val xPathTradeLink: By = By.xpath("//input[@class='trade_offer_access_url']")

class SteamAuthHandler(
    private val webDriver: WebDriver,
    private val webDriverWait: WebDriverWait,
) {

    fun getProfileProperty(steamProperty: SteamProperty, password: String): ProfileProperty {
        inputCredentials(steamProperty.accountName, password)
        guardPageClick()
        inputGuard(steamProperty.getSteamGuard())

        val profileData = getProfileData(password)
        profileData.steam = steamProperty
        return profileData
    }

    fun auth(property: ProfileProperty) {
        inputCredentials(property.steam!!.accountName, property.password)
        guardPageClick()
        inputGuard(property.steam!!.getSteamGuard())
    }

    private fun inputCredentials(
        username: String,
        password: String
    ) {
        webDriver.get(STEAM_AUTH_LINK)
        // Ищем два текстовых поля, Логин и пароль
        val input = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xPathInput))

        input[0].sendKeys(username) // Вводим логин
        input[1].sendKeys(password) // Вводим пароль

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathButton)).click() // Жмем на кнопку авторизации
    }

    private fun guardPageClick() {
        try {
            webDriver.findElement(xPathGuardClick).click()
        } catch (ignored: Exception) {}
    }

    private fun inputGuard(guard: String) {
        val elements = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xPathGuard))
        for (i in 0 until 5) {
            elements[i].click()
            elements[i].sendKeys(guard[i].toString())
        }
    }

    private fun getProfileData(password: String): ProfileProperty {

        val name = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathName)).text

        val files = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathAvatar))
            .findElements(By.tagName("img"))

        var frame: String? = null
        var avatar: String? = null
        if (files.size == 2) {
            frame = files[0].getAttribute("src")
            avatar = files[1].getAttribute("src")
        } else if (files.size == 1) {
            avatar = files[0].getAttribute("src")
        }

        webDriver.get(webDriver.currentUrl + "/tradeoffers/privacy")
        val tradeLink = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathTradeLink))
            .getAttribute("value")

        return ProfileProperty(
            name = name,
            avatar = avatar!!,
            frame = frame,
            tradeLink = tradeLink,
            password = password
        )
    }

}