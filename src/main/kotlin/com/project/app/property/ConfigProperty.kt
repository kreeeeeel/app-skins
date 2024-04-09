package com.project.app.property

import com.project.app.property.type.BrowserType

data class ConfigProperty(
    var browser: BrowserType = BrowserType.FIREFOX,
    var email: String? = null,
    var phone: String? = null,
    var card: CardProperty? = null,
    var isTrayEnabled: Boolean? = null,
)

data class CardProperty(
    var number: String? = null,
    var name: String? = null,
)
