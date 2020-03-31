package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.CloudClientType
import eu.thesimplecloud.api.directorypaths.DirectoryPaths
import eu.thesimplecloud.api.network.packets.service.PacketIORemoveCloudService
import eu.thesimplecloud.api.network.packets.service.PacketIOUpdateCloudService
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
        CloudAPI.instance.getCloudServiceManager().updateCloudService(this.cloudService)

        TemplateCopier().copyTemplate(cloudService, cloudService.getTemplate())

        val serviceConfigurator = Wrapper.instance.serviceConfigurationManager.getServiceConfigurator(cloudService.getServiceVersion().serviceVersionType)
        serviceConfigurator
                ?: throw IllegalStateException("No ServiceConfiguration found by version type: ${cloudService.getServiceVersion().serviceVersionType}")

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
                    Wrapper.instance.communicationClient.sendUnitQuery(PacketOutScreenMessage(CloudClientType.SERVICE, getCloudService(), s))
                    Launcher.instance.logger.console("[${cloudService.getName()}]$s")
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
        Wrapper.instance.cloudServiceProcessManager.unregisterServiceProcess(this)
        this.cloudService.setOnlinePlayers(0)
        this.cloudService.setState(ServiceState.CLOSED)
        if (Wrapper.instance.communicationClient.isOpen()) {
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudService(this.cloudService)).awaitUninterruptibly()
            CloudAPI.instance.getCloudServiceManager().removeCloudService(this.cloudService.getName())
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIORemoveCloudService(this.cloudService.getName()))
            Wrapper.instance.updateWrapperData()
        }

        if (!cloudService.isStatic()) {
            while (true) {
                try {
                    FileUtils.deleteDirectory(this.serviceTmpDir)
                    break
                } catch (e: Exception) {

                }
            }
        }
        Wrapper.instance.portManager.setPortUnused(this.cloudService.getPort())
    }

    private fun createProcessBuilder(jarFile: File): ProcessBuilder {
        val allDependencyPaths = DependencyLoader.INSTANCE.getLoadedDependencies().filter { it.groupId != "eu.thesimplecloud.clientserverapi" }.map { it.getDownloadedFile().absolutePath }
        val classPathValueList = listOf(jarFile.absolutePath).union(allDependencyPaths)
        val separator = if (CloudAPI.instance.isWindows()) ";" else ":"
        val beginAndEnd = if (CloudAPI.instance.isWindows()) "\"" else ""
        val classPathValue = beginAndEnd + classPathValueList.joinToString(separator) + beginAndEnd

        return ProcessBuilder("java", "-Dcom.mojang.eula.agree=true", "-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode",
                "-XX:-UseAdaptiveSizePolicy", "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xms" + cloudService.getMaxMemory() + "M", "-Xmx" + cloudService.getMaxMemory() + "M", "-cp", classPathValue,
                ManifestLoader.getMainClass(jarFile.absolutePath))
    }

    override fun forceStop() {
        process?.destroyForcibly()
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
        return getCloudService().closedPromise()
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