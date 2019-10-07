package eu.thesimplecloud.launcher.startup

import eu.thesimplecloud.launcher.application.CloudApplicationStarter
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.logger.LoggerProvider
import eu.thesimplecloud.launcher.setups.LanguageSetup
import eu.thesimplecloud.launcher.setups.StartSetup
import eu.thesimplecloud.launcher.directorypaths.DirectoryPathManager
import eu.thesimplecloud.lib.language.LanguageManager
import kotlin.system.exitProcess


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher(val launcherStartArguments: LauncherStartArguments) {

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
    val languageManager: LanguageManager

    init {
        instance = this
        Thread.setDefaultUncaughtExceptionHandler { thread, cause -> logger.exception(cause) }
        System.setProperty("user.language", "en")
        DirectoryPathManager()
        commandManager = CommandManager()
        consoleManager = ConsoleManager(commandManager, consoleSender)
        languageManager = LanguageManager("en_EN")
    }

    fun start() {

        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.startThread()
        if (!languageManager.doesFileExist())
            setupManager.queueSetup(LanguageSetup())
        if (launcherStartArguments.startApplication == null)
            setupManager.queueSetup(StartSetup())

        languageManager.loadFile()
        logger.updatePrompt(false)
    }

    fun startApplication(cloudApplicationType: CloudApplicationType){
        ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        logger.info("Starting ${cloudApplicationType.name}...")
        CloudApplicationStarter().startCloudApplication(cloudApplicationType)
    }

    fun shutdown(){
        activeApplication?.shutdown()
        exitProcess(0)
    }


}