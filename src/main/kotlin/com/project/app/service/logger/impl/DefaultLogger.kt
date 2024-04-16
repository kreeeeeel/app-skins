package com.project.app.service.logger.impl

import com.project.app.service.logger.Logger
import java.io.File
import java.io.FileWriter
import java.time.OffsetDateTime

private val LOGS_FOLDER = System.getProperty("user.dir") + "/logs"

class DefaultLogger: Logger {

    override fun debug(message: String) = output(message, LogLevel.DEBUG)
    override fun info(message: String) = output(message, LogLevel.INFO)
    override fun warning(message: String) = output(message, LogLevel.WARNING)
    override fun error(message: String) = output(message, LogLevel.ERROR)

    private fun output(message: String, logLevel: LogLevel) {

        val currentDate = OffsetDateTime.now()
        val pathFile = String.format("%02d-%02d-%d.txt", currentDate.year, currentDate.month.value, currentDate.dayOfMonth)

        val text = String.format("[%02dч %02dм %02dс] - %s: %s%s",
            currentDate.hour,
            currentDate.minute,
            currentDate.second,
            logLevel,
            message,
            System.lineSeparator()
        )

        val directory = File(LOGS_FOLDER)
        if (!directory.exists() && !directory.mkdir()) {
            info("Создание каталога с логами..")
        }

        val file = File(String.format("%s/%s", LOGS_FOLDER, pathFile))
        if (!file.exists() && file.createNewFile()) {
            info("Создание текущего файла логирования..")
        }

        print(text)
        FileWriter(file, true).use { writer ->
            writer.write(text)
            writer.flush()
        }
    }
}

private enum class LogLevel{
    INFO, DEBUG, WARNING, ERROR
}