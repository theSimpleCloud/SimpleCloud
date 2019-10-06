package eu.thesimplecloud.launcher.startup

import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.dependency.LauncherDependencyLoader
import eu.thesimplecloud.launcher.logger.LoggerProvider
import eu.thesimplecloud.lib.directorypaths.DirectoryPathManager
import eu.thesimplecloud.lib.language.LanguageManager
import kotlin.system.exitProcess


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

    var activeApplication: ICloudApplication? = null
    set(value) {
        field = value
        logger.applicationName = value?.getApplicationName().toString()
    }
    val logger = LoggerProvider("Launcher")
    val commandManager: CommandManager
    val consoleSender = ConsoleSender()
    val consoleManager: ConsoleManager
    val setupManager = SetupManager(this)
    val languageManager = LanguageManager("en_EN")

    init {
        instance = this
        System.setProperty("user.language", "en")
        LauncherDependencyLoader().loadLauncherDependencies()
        DirectoryPathManager()
        languageManager.loadFile()
        commandManager = CommandManager()
        consoleManager = ConsoleManager(commandManager, consoleSender)
    }

    fun start() {



        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.startThread()
        if (args.isEmpty()) {
            //setupManager.startSetup(eu.thesimplecloud.launcher.setups.StartSetup())
        }

        logger.updatePromt(false)
    }

    fun shutdown(){
        activeApplication?.shutdown()
        exitProcess(0)
    }


}