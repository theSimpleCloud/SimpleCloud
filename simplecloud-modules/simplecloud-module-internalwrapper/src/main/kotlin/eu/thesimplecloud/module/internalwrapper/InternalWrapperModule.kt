/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.internalwrapper

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.wrapper.impl.DefaultWrapperInfo
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.internalwrapper.setup.InternalWrapperMemorySetup
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
            Launcher.instance.setupManager.queueSetup(InternalWrapperMemorySetup(config))
            Launcher.instance.setupManager.waitFroAllSetups()
        }

        thread(start = true, isDaemon = false) {
            Launcher.instance.consoleSender.sendMessage("module.internalwrapper.starting", "Starting internal wrapper...")
            val processBuilder = ProcessBuilder("java", "-jar", launcherJarFile.absolutePath, "--start-application=WRAPPER", "--disable-auto-updater")
            processBuilder.directory(File("."))
            val process = processBuilder.start()
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