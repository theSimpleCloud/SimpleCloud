package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.base.wrapper.process.filehandler.TemplateCopier
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.client.packets.PacketOutScreenMessage
import eu.thesimplecloud.launcher.startup.Launcher
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
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit


class CloudServiceProcess(private val cloudService: ICloudService) : ICloudServiceProcess {

    private var process: Process? = null
    private val serviceTmpDir = if (cloudService.isStatic()) File(DirectoryPaths.paths.staticPath + cloudService.getName()) else File(DirectoryPaths.paths.tempPath + cloudService.getName())

    override fun start() {
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
        Wrapper.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudService(this.cloudService))


        if (!cloudService.isStatic() || !this.serviceTmpDir.exists())
            TemplateCopier().copyTemplate(cloudService, cloudService.getTemplate())

        val serviceConfigurator = Wrapper.instance.serviceConfigurationManager.getServiceConfigurator(cloudService.getServiceVersion().serviceVersionType)
        serviceConfigurator
                ?: throw IllegalStateException("No ServiceConfiguration found by version type: ${cloudService.getServiceVersion().serviceVersionType}")

        serviceConfigurator.configureService(cloudService, this.serviceTmpDir)
        val jarFile = Wrapper.instance.serviceVersionLoader.loadVersionFile(cloudService.getServiceVersion())
        val processBuilder = createProcessBuilder(jarFile)
        processBuilder.directory(this.serviceTmpDir)
        this.process = processBuilder.start()
        val process = this.process ?: return

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
    }

    private fun processStopped() {
        Launcher.instance.consoleSender.sendMessage("wrapper.service.stopped", "Service %NAME%", cloudService.getName(), " was stopped.")
        Wrapper.instance.cloudServiceProcessManager.unregisterServiceProcess(this)
        this.cloudService.setOnlinePlayers(0)
        this.cloudService.setState(ServiceState.CLOSED)
        if (Wrapper.instance.communicationClient.isOpen()) {
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIOUpdateCloudService(this.cloudService)).awaitUninterruptibly()
            CloudAPI.instance.getCloudServiceManager().removeCloudService(this.cloudService.getName())
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIORemoveCloudService(this.cloudService.getName()))
            Wrapper.instance.updateUsedMemory()
        }

        while (true) {
            try {
                FileUtils.deleteDirectory(this.serviceTmpDir)
                break
            } catch (e: Exception) {

            }
        }
        Wrapper.instance.portManager.setPortUnused(this.cloudService.getPort())
    }

    private fun createProcessBuilder(jarFile: File): ProcessBuilder {
        val launcherJarPath = File(Launcher::class.java.protectionDomain.codeSource.location.toURI()).path
        val baseJarPath = File(this::class.java.protectionDomain.codeSource.location.toURI()).path
        val dependenciesDir = File("dependencies").absolutePath + "/*"
        val classPathValueList = listOf(jarFile.absolutePath, launcherJarPath, baseJarPath, dependenciesDir)
        val separator = if (CloudAPI.instance.isWindows()) ";" else ":"
        val beginAndEnd = if(CloudAPI.instance.isWindows()) "\"" else ""
        val classPathValue = beginAndEnd + classPathValueList.joinToString(separator) + beginAndEnd

        val processBuilder = ProcessBuilder("java", "-Dcom.mojang.eula.agree=true", "-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode",
                "-XX:-UseAdaptiveSizePolicy", "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xms" + this.cloudService.getMaxMemory() + "M", "-Xmx" + this.cloudService.getMaxMemory() + "M", "-cp", classPathValue,
                ManifestLoader.getMainClass(jarFile.absolutePath))
        Launcher.instance.consoleSender.sendMessage(processBuilder.command().joinToString(" "))
        return processBuilder
    }

    override fun forceStop() {
        process?.destroyForcibly()
    }

    override fun isActive(): Boolean = this.process?.isAlive ?: false

    override fun shutdown() {
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