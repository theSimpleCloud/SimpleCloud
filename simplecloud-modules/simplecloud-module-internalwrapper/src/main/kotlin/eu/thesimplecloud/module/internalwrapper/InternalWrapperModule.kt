package eu.thesimplecloud.module.internalwrapper

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class InternalWrapperModule : ICloudModule {

    private var process: Process? = null
    private var commandExecutable: ICommandExecutable? = null

    override fun onEnable() {
        val launcherJarFile = Launcher.instance.getLauncherFile()
        val wrapperManager = CloudAPI.instance.getWrapperManager()
        val config = Launcher.instance.launcherConfigLoader.loadConfig()

        if (wrapperManager.getWrapperByHost(config.host) == null) {
            val wrapperInfo = DefaultWrapperInfo("InternalWrapper", config.host, 2, 2048)
            CloudAPI.instance.getWrapperManager().updateWrapper(wrapperInfo)
        }
        thread(start = true, isDaemon = false) {
            Launcher.instance.consoleSender.sendMessage("moudle.internalwrapper.starting", "Starting internal wrapper...")
            val processBuilder = ProcessBuilder("java", "-jar", launcherJarFile.absolutePath, "--start-application=WRAPPER", "--disable-auto-updater")
            processBuilder.directory(File("."))
            val process = processBuilder.start() ?: return@thread
            this.process = process
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val commandExecutable = getCommandExecutable(process)
            this.commandExecutable = commandExecutable
            while (process.isAlive) {
                val readLine = bufferedReader.readLine() ?: continue
                Launcher.instance.screenManager.addScreenMessage(commandExecutable, readLine)
            }
            Launcher.instance.screenManager.unregisterScreen(commandExecutable.getName())
        }
    }

    private fun getCommandExecutable(process: Process): ICommandExecutable {
        return object : ICommandExecutable {

            override fun getName(): String = "InternalWrapperConsole"

            override fun executeCommand(command: String) {
                val command = command + "\n"
                try {
                    if (process.outputStream != null) {
                        process.outputStream?.write(command.toByteArray())
                        process.outputStream?.flush()
                    }
                } catch (e: IOException) {
                    Launcher.instance.consoleSender.sendMessage("[InternalWrapper] Outputstream is closed.")
                }

            }

        }
    }

    override fun onDisable() {
        if (process?.isAlive == true) {
            this.commandExecutable?.executeCommand("stop")
            Launcher.instance.scheduler.schedule({
                if (process?.isAlive == true)
                    process?.destroyForcibly()
            }, 13, TimeUnit.SECONDS)
            process?.waitFor()
        }
    }

    override fun isReloadable(): Boolean = false

}