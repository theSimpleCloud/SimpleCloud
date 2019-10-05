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
class Launcher {

    companion object {
        lateinit var instance: Launcher
    }

    val logger = LoggerProvider("Launcher")
    val commandManager = CommandManager()
    val consoleManager = ConsoleManager(commandManager)

    fun start() {
        instance = this
        System.setProperty("user.language", "en")

        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.startThread()
    }


}