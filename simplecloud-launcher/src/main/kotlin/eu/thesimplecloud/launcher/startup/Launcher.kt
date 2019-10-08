package eu.thesimplecloud.launcher.startup

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.launcher.application.CloudApplicationStarter
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.logging.LoggerProvider
import eu.thesimplecloud.launcher.setups.LanguageSetup
import eu.thesimplecloud.launcher.setups.StartSetup
import eu.thesimplecloud.launcher.directorypaths.DirectoryPathManager
import eu.thesimplecloud.lib.language.LanguageManager
import java.util.function.Consumer
import kotlin.system.exitProcess


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher(val launcherStartArguments: LauncherStartArguments) : IBootstrap {

    companion object {
        lateinit var instance: Launcher
    }

    var activeApplication: ICloudApplication? = null
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

    override fun start() {
        languageManager.loadFile()
        commandManager.registerAllCommands("eu.thesimplecloud.launcher.commands")
        consoleManager.startThread()
        if (!languageManager.fileExistBeforeLoad())
            setupManager.queueSetup(LanguageSetup())
        if (launcherStartArguments.startApplication == null)
            setupManager.queueSetup(StartSetup())
        logger.updatePrompt(false)
        setupManager.onAllSetupsCompleted(Consumer { this.launcherStartArguments.startApplication?.let { startApplication(it) } })
    }

    fun startApplication(cloudApplicationType: CloudApplicationType) {
        ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        logger.info("Starting ${cloudApplicationType.getApplicationName()}...")
        CloudApplicationStarter().startCloudApplication(cloudApplicationType)
    }

    override fun shutdown() {
        activeApplication?.shutdown()
        exitProcess(0)
    }

    override fun isActive(): Boolean = true


}