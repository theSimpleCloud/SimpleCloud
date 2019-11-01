package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.base.wrapper.process.filehandler.ServiceVersionLoader
import eu.thesimplecloud.base.wrapper.process.filehandler.TemplateCopier
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.network.packets.service.PacketIORemoveCloudService
import eu.thesimplecloud.lib.network.packets.service.PacketIOUpdateCloudService
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.lib.service.impl.DefaultCloudService
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudProxyGroup
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.IllegalStateException
import kotlin.concurrent.thread

class CloudServiceProcess(private val cloudService: ICloudService) : ICloudServiceProcess {

    private var process: Process? = null

    override fun start() {
        Wrapper.instance.cloudServiceProcessManager.registerServiceProcess(this)
        Launcher.instance.consoleSender.sendMessage("wrapper.service.starting", "Starting service %NAME%", cloudService.getName(), ".")
        cloudService as DefaultCloudService
        if (cloudService.getServiceType().isProxy()) {
            val proxyGroup = cloudService.getServiceGroup()
            proxyGroup as ICloudProxyGroup
            cloudService.setPort(Wrapper.instance.portManager.getUnusedPort(proxyGroup.getStartPort()))
        } else {
            cloudService.setPort(Wrapper.instance.portManager.getUnusedPort())
        }
        cloudService.setState(ServiceState.STARTING)
        CloudLib.instance.getCloudServiceManger().updateCloudService(cloudService)
        val serviceTmpDir = if (cloudService.isStatic()) File(DirectoryPaths.paths.staticPath + cloudService.getName()) else File(DirectoryPaths.paths.tempPath + cloudService.getName())
        if (!cloudService.isStatic() || !serviceTmpDir.exists())
            TemplateCopier().copyTemplate(cloudService, cloudService.getTemplate())
        val serviceConfigurator = Wrapper.instance.serviceConfigurationManager.getServiceConfigurator(cloudService.getServiceVersion().serviceVersionType)
        serviceConfigurator
                ?: throw IllegalStateException("No ServiceConfiguration found by version type: ${cloudService.getServiceVersion().serviceVersionType}")
        serviceConfigurator.configureService(cloudService, serviceTmpDir)
        val jarFile = ServiceVersionLoader().loadVersionFile(cloudService.getServiceVersion())
        val processBuilder = ProcessBuilder("java", "-Dcom.mojang.eula.agree=true", "-XX:+UseConcMarkSweepGC", "-XX:+CMSIncrementalMode",
                "-XX:-UseAdaptiveSizePolicy", "-Djline.terminal=jline.UnsupportedTerminal",
                "-Xms" + this.cloudService.getMaxMemory() + "M", "-Xmx" + this.cloudService.getMaxMemory() + "M", "-jar",
                jarFile.absolutePath)
        processBuilder.directory(serviceTmpDir)
        this.process = processBuilder.start()
        val process = this.process ?: return

        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        while (process.isAlive) {
            try {
                val s = bufferedReader.readLine() ?: continue
                if (!s.equals("", ignoreCase = true) && !s.equals(" ", ignoreCase = true) && !s.equals(">", ignoreCase = true)
                        && !s.equals(" >", ignoreCase = true) && !s.contains("InitialHandler has connected")) {
                    Launcher.instance.logger.console("[${cloudService.getName()}]$s")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Launcher.instance.consoleSender.sendMessage("wrapper.service.stopped", "Service %NAME%", cloudService.getName(), " was stopped.")
        Wrapper.instance.cloudServiceProcessManager.unregisterServiceProcess(this)
        Wrapper.instance.updateUsedMemory()
        this.cloudService.setOnlinePlayers(0)
        this.cloudService.setState(ServiceState.CLOSED)
        Wrapper.instance.communicationClient.sendQuery(PacketIOUpdateCloudService(this.cloudService))
        Wrapper.instance.communicationClient.sendQuery(PacketIORemoveCloudService(this.cloudService.getName()))
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
            thread {
                val startTime = System.currentTimeMillis()
                while (true) {
                    if (!isActive())
                        break

                    if (startTime + 7000 < System.currentTimeMillis())
                        forceStop()
                    Thread.sleep(200)
                }
            }
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