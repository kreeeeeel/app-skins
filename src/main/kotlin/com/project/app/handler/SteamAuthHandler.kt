package com.project.app.handler

import com.project.app.property.ProfileProperty
import com.project.app.property.SteamProperty
import com.project.app.webdriver.BrowseDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

private const val STEAM_AUTH_LINK = "https://steamcommunity.com/login/home/?goto="

val xPathInput: By = By.xpath("//input[@class='_2eKVn6g5Yysx9JmutQe7WV']")
val xPathButton: By = By.xpath("//button[@class='_2QgFEj17t677s3x299PNJQ']")
val xPathGuard: By = By.xpath("//input[@class='HPSuAjHOkNfMHwURXTns7 Focusable']")
val xPathGuardClick: By = By.xpath("//div[@class='_1cnUQ3RrQgqaLodJp0Ljnt _2meUB-qHRC9f-cX8Rw8QaZ']")

class SteamAuthHandler {

    fun getProfileProperty(steamProperty: SteamProperty, password: String): SteamProperty? {

        var profileProperty: ProfileProperty? = null
        val browseDriver = BrowseDriver().getDriver()
        val webDriverWait = WebDriverWait(browseDriver, Duration.ofSeconds(10))

        try {
            // Переходим по ссылке авторизации
            browseDriver.get(STEAM_AUTH_LINK)
            // Ищем два текстовых поля, Логин и пароль
            val input = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xPathInput))

            input[0].sendKeys(steamProperty.accountName) // Вводим логин
            input[1].sendKeys(password) // Вводим пароль

            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathButton)).click() // Жмем на кнопку авторизации

            guardPageClick(browseDriver)
            inputGuard(webDriverWait, steamProperty.getSteamGuard())
        } finally {
            browseDriver.quit()
        }
        return null
    }

    private fun guardPageClick(webDriver: WebDriver) {
        try {
            webDriver.findElement(xPathGuardClick).click()
        } catch (ignored: Exception) {}
    }

    private fun inputGuard(webDriverWait: WebDriverWait, guard: String) {
        val elements = webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(xPathGuard))
        for (i in 0 until 5) {
            elements[i].click()
            elements[i].sendKeys(guard[i].toString())
        }
    }

}