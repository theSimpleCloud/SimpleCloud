package eu.thesimplecloud.launcher

import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.logger.LoggerProvider
import eu.thesimplecloud.lib.directorypaths.DirectoryPathManager
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.language.LanguageManager


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher(val args: Array<String>) {

    companion object {
        lateinit var instance: Launcher
    }

    val logger = LoggerProvider("Launcher")
    val commandManager = CommandManager()
    val consoleManager = ConsoleManager(commandManager)
    val setupManager = SetupManager(this)
    val languageManager = LanguageManager("en_EN")

    fun start() {
        instance = this
        System.setProperty("user.language", "en")
        DirectoryPathManager()
        languageManager.loadFile()

        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.startThread()
        if (args.size == 0) {
            //setupManager.startSetup(eu.thesimplecloud.launcher.setups.StartSetup())
        }

        logger.updatePromt(false)
    }


}