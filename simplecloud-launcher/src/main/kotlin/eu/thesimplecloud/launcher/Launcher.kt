package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.application.CloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.logger.LoggerProvider

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher : CloudApplication {

    companion object {
        lateinit var instance: Launcher
    }

    private var running = true
    val logger = LoggerProvider(this)
    val consoleManager = ConsoleManager(this)

    override fun start() {
        instance = this

        System.setProperty("user.language", "en")
        consoleManager.start()

        logger.success("begin")
        logger.info("begin")
        logger.warning("begin")
        logger.severe("begin")
        logger.console("begin")
        logger.empty("begin")
    }

    override fun shutdown() {
        running = false
    }

    override fun getApplicationName(): String = "Launcher"


    override fun isRunning(): Boolean = running


}