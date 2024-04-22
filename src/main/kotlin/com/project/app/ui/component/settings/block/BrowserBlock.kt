package com.project.app.ui.component.settings.block

import com.project.app.data.type.BrowserType
import com.project.app.models.ConfigModel
import com.project.app.ui.component.settings.SettingBlock
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

class BrowserBlock(configModel: ConfigModel):
    SettingBlock("Браузер", "browser", configModel) {

    private val text = Label("Выберите браузер, который будет использоваться программой").also {
        it.id = "browserHint"
        it.layoutX = 21.0
        it.layoutY = 47.0
    }

    private val google = getBrowser(BrowserType.GOOGLE)
    private val edge = getBrowser(BrowserType.EDGE)
    private val firefox = getBrowser(BrowserType.FIREFOX)

    override fun init(): Double {
        block.children.addAll(text, google, edge, firefox)
        block.prefHeight = 240.0
        redrawing()
        return block.prefHeight
    }

    private fun redrawing() {
        google.isDisable = config.browserType == BrowserType.GOOGLE
        edge.isDisable = config.browserType == BrowserType.EDGE || config.browserType == BrowserType.NONE
        firefox.isDisable = config.browserType == BrowserType.FIREFOX
    }

    private fun getBrowser(browserType: BrowserType) = Pane().also {

        it.id = "browserPane"
        it.layoutX = 15.0
        it.layoutY = if (browserType == BrowserType.GOOGLE) 95.0
        else (if (browserType == BrowserType.FIREFOX) 185.0 else 140.0)

        val icon = ImageView().also { img ->
            img.id = if (browserType == BrowserType.GOOGLE) "google"
            else (if (browserType == BrowserType.FIREFOX) "firefox" else "edge")
            img.layoutX = if (browserType == BrowserType.FIREFOX) 12.0 else 8.0
            img.layoutY = if (browserType == BrowserType.FIREFOX) 8.0 else 4.0
            img.fitWidth = if (browserType == BrowserType.FIREFOX) 26.0 else 32.0
            img.fitHeight = img.fitWidth
        }

        val text = Label().also { l ->
            l.id = "browserText"
            l.text = if (browserType == BrowserType.GOOGLE) "Google Chrome"
            else (if (browserType == BrowserType.FIREFOX) "Mozilla Firefox" else "Microsoft Edge")
            l.layoutX = 69.0
            l.layoutY = 10.0
        }

        it.setOnMouseClicked {
            config.browserType = browserType
            config.save()

            redrawing()
        }
        it.children.addAll(icon, text)
    }

}