package com.project.app.property

import com.project.app.property.type.BrowserType

data class ConfigProperty(
    val browser: BrowserType = BrowserType.FIREFOX
)
