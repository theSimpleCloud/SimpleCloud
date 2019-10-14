package eu.thesimplecloud.launcher.startup

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.launcher.application.ApplicationStarter
import eu.thesimplecloud.launcher.module.CloudModuleLoader
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.logging.LoggerProvider
import eu.thesimplecloud.launcher.setups.LanguageSetup
import eu.thesimplecloud.launcher.setups.StartSetup
import eu.thesimplecloud.launcher.config.LauncherConfigLoader
import eu.thesimplecloud.launcher.setups.AutoIpSetup
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.external.ICloudModule
import eu.thesimplecloud.lib.language.LanguageManager
import java.util.concurrent.Executors
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

    val launcherCloudModule = object : ICloudModule {
        override fun onEnable() {
        }

        override fun onDisable() {
        }
    }
    var activeApplication: ICloudApplication? = null
    val logger = LoggerProvider("Launcher")
    val commandManager: CommandManager
    val consoleSender = ConsoleSender()
    val consoleManager: ConsoleManager
    val setupManager = SetupManager(this)
    val languageManager: LanguageManager
    val launcherConfigLoader = LauncherConfigLoader()
    val scheduler = Executors.newScheduledThreadPool(1)

    init {
        instance = this
        Thread.setDefaultUncaughtExceptionHandler { thread, cause -> this.logger.exception(cause) }
        System.setProperty("user.language", "en")
        val launcherConfig = this.launcherConfigLoader.loadConfig()
        DirectoryPaths.paths = launcherConfig.directoryPaths
        this.commandManager = CommandManager()
        this.consoleManager = ConsoleManager(this.commandManager, this.consoleSender)
        this.languageManager = LanguageManager("en_EN")
    }

    override fun start() {
        this.languageManager.loadFile()
        this.commandManager.registerAllCommands(launcherCloudModule, "eu.thesimplecloud.launcher.commands")
        this.consoleManager.startThread()
        if (!this.languageManager.fileExistBeforeLoad())
            this.setupManager.queueSetup(LanguageSetup())
        if (!this.launcherConfigLoader.doesConfigFileExist())
            this.setupManager.queueSetup(AutoIpSetup())
        if (this.launcherStartArguments.startApplication == null)
            this.setupManager.queueSetup(StartSetup())


        this.logger.updatePrompt(false)
        this.setupManager.onAllSetupsCompleted(Consumer { this.launcherStartArguments.startApplication?.let { startApplication(it) } })
    }

    fun startApplication(cloudApplicationType: CloudApplicationType) {
        ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        logger.info("Starting ${cloudApplicationType.getApplicationName()}...")
        ApplicationStarter().startApplication(cloudApplicationType)
    }

    override fun shutdown() {
        activeApplication?.onDisable()
        exitProcess(0)
    }

    override fun isActive(): Boolean = true


}