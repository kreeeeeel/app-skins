package com.project.app.task

import com.project.app.handler.DriverHandler
import com.project.app.repository.ConfigRepository
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import java.util.*
import java.util.concurrent.Executors

private const val WAIT_TASK = "Просмотр аккаунтов через %02d ч. %02d мин. %02d сек."
private const val START_TASK = "Проверка аккаунтов %02d мин. %02d сек."
private const val FINISHED_TASK = "Приложение завершило работу, смотрим инвентарь"

private const val WAIT_ICON = "red"
private const val START_ICON = "green"
private const val FINISHED_ICON = "orange"

enum class TaskStatus {
    WAIT, STARTED, FINISHED
}

class Task(
    val label: Label,
    val image: ImageView,
    val timer: Timer = Timer(),
    var status: TaskStatus = TaskStatus.WAIT
) {

    fun run() {
        timer.scheduleAtFixedRate(TaskTimer(this), 1000, 1000)
    }

}

class TaskTimer(
    private val task: Task
): TimerTask() {

    private var seconds: Int? = null

    override fun run() {
        try {
            if (seconds == null) {
                val configRepository = ConfigRepository()
                val config = configRepository.find()

                task.status = TaskStatus.WAIT
                seconds = config.hourChecked * 3600
            }

            val remainingSeconds = if (task.status == TaskStatus.STARTED) seconds!! + 1 else seconds!! - 1
            if (remainingSeconds <= 0) {

                if (task.status == TaskStatus.WAIT) {
                    task.status = TaskStatus.STARTED

                    val executors = Executors.newSingleThreadExecutor()
                    executors.submit {
                        val driverHandler = DriverHandler()
                        driverHandler.startTask()
                        task.status = TaskStatus.FINISHED
                        seconds = 5
                    }
                    executors.shutdown()
                }

                if (task.status == TaskStatus.FINISHED) {
                    task.status = TaskStatus.WAIT
                    seconds = null
                    return
                }

            }

            seconds = remainingSeconds
            editUI(task.status)

        } catch (e: Exception) {
            task.timer.cancel()
        }
    }

    private fun editUI(taskStatus: TaskStatus) {
        val text = when (taskStatus) {
            TaskStatus.WAIT -> {
                val remainingHours = seconds!! / 3600
                val remainingMinutes = seconds!! % 3600 / 60
                val remainingSeconds = seconds!! % 60
                String.format(WAIT_TASK, remainingHours, remainingMinutes, remainingSeconds)
            }
            TaskStatus.STARTED -> {
                val remainingMinutes = seconds!! % 3600 / 60
                val remainingSeconds = seconds!! % 60
                String.format(START_TASK, remainingMinutes, remainingSeconds)
            }
            TaskStatus.FINISHED -> FINISHED_TASK
        }

        val icon = when(taskStatus) {
            TaskStatus.WAIT -> WAIT_ICON
            TaskStatus.STARTED -> START_ICON
            TaskStatus.FINISHED -> FINISHED_ICON
        }

        try {
            Platform.runLater {
                task.label.text = text
                task.image.id = icon
            }
        } catch (ignored: Exception) {}

    }
}