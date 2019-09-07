package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.logger.LoggerProvider

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher : ICloudApplication {

    companion object {
        lateinit var instance: Launcher
    }

    private var running = true
    val logger = LoggerProvider(this)
    val commandManager = CommandManager(this)
    val consoleManager = ConsoleManager(this, commandManager)

    override fun start() {
        instance = this
        System.setProperty("user.language", "en")

        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.start()
    }

    override fun shutdown() {
        running = false
    }

    override fun getApplicationName(): String = "Launcher"


    override fun isRunning(): Boolean = running


}