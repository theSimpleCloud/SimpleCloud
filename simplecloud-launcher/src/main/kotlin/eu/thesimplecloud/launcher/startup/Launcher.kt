package eu.thesimplecloud.launcher.startup

import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.language.LanguageManager
import eu.thesimplecloud.api.utils.ManifestLoader
import eu.thesimplecloud.launcher.LauncherMain
import eu.thesimplecloud.launcher.application.ApplicationStarter
import eu.thesimplecloud.launcher.application.CloudApplicationType
import eu.thesimplecloud.launcher.application.ICloudApplication
import eu.thesimplecloud.launcher.config.LauncherConfigLoader
import eu.thesimplecloud.launcher.console.ConsoleManager
import eu.thesimplecloud.launcher.console.ConsoleSender
import eu.thesimplecloud.launcher.console.command.CommandManager
import eu.thesimplecloud.launcher.console.setup.SetupManager
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.invoker.MethodInvokeHelper
import eu.thesimplecloud.launcher.logging.LoggerProvider
import eu.thesimplecloud.launcher.screens.IScreenManager
import eu.thesimplecloud.launcher.screens.ScreenManagerImpl
import eu.thesimplecloud.launcher.setups.AutoIpSetup
import eu.thesimplecloud.launcher.setups.LanguageSetup
import eu.thesimplecloud.launcher.setups.StartSetup
import eu.thesimplecloud.launcher.updater.LauncherUpdater
import eu.thesimplecloud.launcher.updater.UpdateExecutor
import java.io.File
import java.io.IOException
import java.net.URLClassLoader
import java.util.concurrent.Executors
import kotlin.system.exitProcess


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 06.09.2019
 * Time: 21:31
 */
class Launcher(val launcherStartArguments: LauncherStartArguments) {

    companion object {
        @JvmStatic
        lateinit var instance: Launcher
            private set
    }

    private val launcherCloudModule = object : ICloudModule {
        override fun onEnable() {
        }

        override fun onDisable() {
        }
    }
    var activeApplication: ICloudApplication? = null
    val screenManager: IScreenManager = ScreenManagerImpl()
    val logger = LoggerProvider("Launcher", screenManager)
    val commandManager: CommandManager
    val consoleSender = ConsoleSender()
    val consoleManager: ConsoleManager
    val setupManager = SetupManager(this)
    val languageManager: LanguageManager
    val launcherConfigLoader = LauncherConfigLoader()
    val scheduler = Executors.newScheduledThreadPool(1)

    init {
        instance = this
        if (System.getProperty("simplecloud.version") == null)
            System.setProperty("simplecloud.version", Launcher::class.java.`package`.implementationVersion)
        Thread.setDefaultUncaughtExceptionHandler { thread, cause -> this.logger.exception(cause) }
        System.setProperty("user.language", "en")
        val launcherConfig = this.launcherConfigLoader.loadConfig()
        DirectoryPaths.paths = launcherConfig.directoryPaths
        this.commandManager = CommandManager()
        this.consoleManager = ConsoleManager(this.commandManager, this.consoleSender)
        this.languageManager = LanguageManager("en_EN")
    }

    fun start() {
        if (Thread.currentThread().contextClassLoader == null) {
            Thread.currentThread().contextClassLoader = ClassLoader.getSystemClassLoader()
        }
        if (!launcherStartArguments.disableAutoUpdater) {
            if (executeUpdateIfAvailable()) {
                return
            }
        } else {
            this.logger.warning("Auto updater is disabled.")
        }
        this.languageManager.loadFile()
        this.commandManager.registerAllCommands(launcherCloudModule, "eu.thesimplecloud.launcher.commands")
        this.consoleManager.startThread()
        if (!this.languageManager.fileExistBeforeLoad())
            this.setupManager.queueSetup(LanguageSetup())
        if (!this.launcherConfigLoader.doesConfigFileExist())
            this.setupManager.queueSetup(AutoIpSetup())
        if (this.launcherStartArguments.startApplication == null)
            this.setupManager.queueSetup(StartSetup())


        this.setupManager.waitFroAllSetups()
        this.launcherStartArguments.startApplication?.let { startApplication(it) }
    }

    private fun executeUpdateIfAvailable(): Boolean {
        val updater = LauncherUpdater()
        if (updater.isUpdateAvailable()) {
            this.consoleSender.sendMessage("Found a new launcher version: " + updater.getLatestVersion()!!)
            UpdateExecutor().executeUpdate(updater)
            return true
        } else {
            this.consoleSender.sendMessage("You are running on the latest version of SimpleCloud.")
        }
        return false
    }

    fun startApplication(cloudApplicationType: CloudApplicationType) {
        clearConsole()
        logger.info("Starting ${cloudApplicationType.getApplicationName()}...")
        ApplicationStarter().startApplication(cloudApplicationType)
    }

    fun clearConsole() {
        if (isWindows()) {
            try {
                ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } else {
            print("\u001b[H\u001b[2J")
            System.out.flush()
        }
    }

    fun executeCommand(command: String) {
        if (command.isBlank()) return
        this.commandManager.handleCommand(command, this.consoleSender)
    }

    fun shutdown() {
        activeApplication?.onDisable()
        exitProcess(0)
    }

    fun isWindows(): Boolean = System.getProperty("os.name").toLowerCase().contains("windows")

    fun getLauncherFile(): File {
        if (System.getProperty("simplecloud.launcher.update-mode") != null) {
            return File("launcher-update.jar")
        }
        return File(Launcher::class.java.protectionDomain.codeSource.location.toURI().path)
    }

}