package com.project.app.handler

import com.project.app.property.InventoryProperties
import com.project.app.property.ItemProperties
import com.project.app.repository.ConfigRepository
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

private const val LIS_SKIN_LINK = "https://lis-skins.ru"

private val xPathAuthButton: By = By.xpath("//a[@class='login-button']")
private val xPathSteamAuthButton: By = By.xpath("//input[@class='btn_green_white_innerfade']")
private val xPathInLogging: By = By.xpath("//div[@class='logined desktop-only']")

private val xPathTrade: By = By.xpath("//input[@id='profile_trade_url']")
private val xPathSaveTrade: By = By.xpath("//div[@id='profile_save_trade_url_button']")

private val xPathCost: By = By.xpath("//span[@class='payout-min-sum-value']")
private val xPathInventory: By = By.xpath("//div[@class='inventory']")
private val xPathName: By = By.xpath("//div[@class='name']")
private val xPathExterior: By = By.xpath("//span[@class='skin-info-exterior']")
private val xPathImage: By = By.xpath("//div[@class='image']")

//private val xPathCloudfare: By = By.xpath("//checkbox[@class='cloudfate-info']")

private const val SUB_LENGTH = "background-image: url(".length

class LisSkinHandler(
    private val webDriver: WebDriver,
    private val webDriverWait: WebDriverWait,
) {

    fun auth(tradeLink: String? = null) {
        webDriver.get(LIS_SKIN_LINK)

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathAuthButton)).click()
        webDriver.switchTo().window(webDriver.windowHandles.elementAt(webDriver.windowHandles.size - 1))

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathSteamAuthButton)).click()
        webDriver.switchTo().window(webDriver.windowHandles.elementAt(webDriver.windowHandles.size - 1))

        if (tradeLink != null) {

            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathInLogging))
            webDriver.get("$LIS_SKIN_LINK/profile")

            val trade = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathTrade))
            trade.clear()
            trade.sendKeys(tradeLink)

            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathSaveTrade)).click()
        }

    }

    fun getInventory(): InventoryProperties? {
        try {
            webDriver.get(LIS_SKIN_LINK)

            val inventory = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(xPathInventory))
            val items = inventory.findElements(By.tagName("div"))
                .map { getItem(it) }

            getCurrentSellCost()
            return InventoryProperties(
                summa = items.sumOf { it.price },
                items = items
            )

        } catch (ex: Exception) {
            return null
        }
    }

    private fun getCurrentSellCost() {
        val configRepository = ConfigRepository()
        val config = configRepository.find()

        val cost = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(xPathCost)).text

        config.currentCostSell = cost.toBigDecimal()
        configRepository.save(config)
    }

    private fun getItem(webElement: WebElement): ItemProperties {

        val assetId = webElement.getAttribute("data-assetid")
        val price = webElement.getAttribute("data-price").toBigDecimal()

        val name = webElement.findElement(xPathName).text
        val exterior = webElement.findElement(xPathExterior).text

        val background = webElement.findElement(xPathImage)
            .getAttribute("style")


        val image = background.substring(SUB_LENGTH, background.length - 1)
        return ItemProperties(assetId, name, image, exterior, price)
    }

}