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

package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.service.impl.DefaultCloudService
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.api.utils.ManifestLoader
import eu.thesimplecloud.base.wrapper.process.filehandler.TemplateCopier
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.client.packets.PacketOutScreenMessage
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.launcher.dependency.DependencyLoader
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class CloudServiceProcess(private val cloudService: ICloudService) : ICloudServiceProcess {

    private var process: Process? = null
    private val serviceTmpDir = if (cloudService.isStatic()) File(DirectoryPaths.paths.staticPath + cloudService.getName()) else File(DirectoryPaths.paths.tempPath + cloudService.getName())

    override fun start(): ICommunicationPromise<Unit> {
        Launcher.instance.consoleSender.sendMessage("wrapper.service.starting", "Starting service %NAME%", cloudService.getName(), ".")
        this.cloudService as DefaultCloudService
        if (cloudService.getServiceType().isProxy()) {
            val proxyGroup = cloudService.getServiceGroup()
            proxyGroup as ICloudProxyGroup
            this.cloudService.setPort(Wrapper.instance.portManager.getUnusedPort(proxyGroup.getStartPort()))
        } else {
            this.cloudService.setPort(Wrapper.instance.portManager.getUnusedPort())
        }
        this.cloudService.setState(ServiceState.STARTING)
        CloudAPI.instance.getCloudServiceManager().update(this.cloudService)

        TemplateCopier().copyTemplate(cloudService, cloudService.getTemplate())

        val serviceConfigurator = Wrapper.instance.serviceConfigurationManager
                .getServiceConfigurator(cloudService.getServiceVersion().serviceAPIType)
        serviceConfigurator
                ?: throw IllegalStateException("No ServiceConfiguration found by api type: ${cloudService.getServiceVersion().serviceAPIType}")

        serviceConfigurator.configureService(cloudService, this.serviceTmpDir)
        val jarFile = Wrapper.instance.serviceVersionLoader.loadVersionFile(cloudService.getServiceVersion())
        val processBuilder = createProcessBuilder(jarFile)
        processBuilder.directory(this.serviceTmpDir)
        val process = processBuilder.start()
        this.process = process

        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        while (process.isAlive) {
            try {
                val s = bufferedReader.readLine() ?: continue
                if (!s.equals("", ignoreCase = true) && !s.equals(" ", ignoreCase = true) && !s.equals(">", ignoreCase = true)
                        && !s.equals(" >", ignoreCase = true) && !s.contains("InitialHandler has pinged")) {
                    Wrapper.instance.communicationClient.sendUnitQuery(PacketOutScreenMessage(NetworkComponentType.SERVICE, getCloudService(), s))
                    //Launcher.instance.logger.console("[${cloudService.getName()}]$s")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        processStopped()
        //this method will not return before the process stops
        return CommunicationPromise.of(Unit)
    }

    private fun processStopped(){
        Launcher.instance.consoleSender.sendMessage("wrapper.service.stopped", "Service %NAME%", cloudService.getName(), " was stopped.")
        deleteTmpFolder()
        if (Wrapper.instance.communicationClient.isOpen()) {
            var tries = 0
            while (this.cloudService.isAuthenticated()) {
                Thread.sleep(100)
                tries++
                if (tries == 30) break
            }
            this.cloudService.setOnlineCount(0)
            this.cloudService.setState(ServiceState.CLOSED)
            CloudAPI.instance.getCloudServiceManager().delete(this.cloudService).awaitUninterruptibly()
            Wrapper.instance.updateWrapperData()
        }

        Wrapper.instance.cloudServiceProcessManager.unregisterServiceProcess(this)
        Wrapper.instance.portManager.setPortUnused(this.cloudService.getPort())
    }

    private fun deleteTmpFolder() {
        if (!cloudService.isStatic()) {
            while (true) {
                try {
                    FileUtils.deleteDirectory(this.serviceTmpDir)
                    break
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun createProcessBuilder(jarFile: File): ProcessBuilder {
        return ProcessBuilder(*getStartCommandArgs(jarFile))
    }

    private fun getStartCommandArgs(jarFile: File): Array<String> {
        val allDependencyPaths = DependencyLoader.INSTANCE.getLoadedDependencies().filter { it.groupId != "eu.thesimplecloud.clientserverapi" }.map { it.getDownloadedFile().absolutePath }
        val classPathValueList = listOf(jarFile.absolutePath).union(allDependencyPaths)
        val separator = if (CloudAPI.instance.isWindows()) ";" else ":"
        val beginAndEnd = if (CloudAPI.instance.isWindows()) "\"" else ""
        val classPathValue = beginAndEnd + classPathValueList.joinToString(separator) + beginAndEnd

        val jvmArguments = Wrapper.instance.jvmArgumentsConfig.jvmArguments.filter { it.groups.contains("all") || it.groups.contains(this.cloudService.getGroupName()) }
        val commands = mutableListOf("java")

        jvmArguments.forEach { commands.addAll(it.arguments) }

        val startArguments = arrayListOf("-Dcom.mojang.eula.agree=true", "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xms" + cloudService.getMaxMemory() + "M", "-Xmx" + cloudService.getMaxMemory() + "M", "-cp", classPathValue,
                ManifestLoader.getMainClassFromManifestFile(jarFile))
        commands.addAll(startArguments)

        val lowerCaseName = cloudService.getServiceVersion().name.toLowerCase()
        if (lowerCaseName.contains("spigot") || lowerCaseName.contains("paper")) {
            commands.add("nogui")
        }
        return commands.toTypedArray()
    }

    override fun forceStop() {
        process?.destroyForcibly()
    }

    override fun getTempDirectory(): File {
        return serviceTmpDir
    }

    override fun isActive(): Boolean = this.process?.isAlive ?: false

    override fun shutdown(): ICommunicationPromise<Unit> {
        if (isActive()) {
            if (this.cloudService.getServiceType().isProxy()) {
                executeCommand("end")
            } else {
                executeCommand("stop")
            }
            Launcher.instance.scheduler.schedule({
                if (isActive())
                    forceStop()
            }, 7, TimeUnit.SECONDS)
        }
        return cloudListener<CloudServiceUnregisteredEvent>()
                .addCondition { it.cloudService == this.cloudService }
                .toUnitPromise()
    }

    override fun executeCommand(command: String) {
        val command = command + "\n"
        try {
            if (process != null && process?.outputStream != null) {
                process?.outputStream?.write(command.toByteArray())
                process?.outputStream?.flush()
            }
        } catch (e: IOException) {
            Launcher.instance.logger.warning("[" + this.cloudService.getName() + "]" + " Outputstream is closed.")
        }

    }


    override fun getCloudService(): ICloudService = cloudService

}