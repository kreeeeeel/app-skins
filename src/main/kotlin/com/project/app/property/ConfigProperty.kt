package com.project.app.property

import com.project.app.property.type.BrowserType
import java.math.BigDecimal

data class ConfigProperty(
    var browser: BrowserType = BrowserType.FIREFOX,
    var email: String? = null,
    var phone: String? = null,
    var card: CardProperty? = null,
    var isTrayEnabled: Boolean? = null,
    var currentCostSell: BigDecimal? = null,
    var hourChecked: Int = 3,
    var attemptSteam: Int = 3,
)

data class CardProperty(
    var number: String? = null,
    var name: String? = null,
)
