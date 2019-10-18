package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.directorypaths.DirectoryPaths
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ServiceState
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class CloudProcess(private val cloudService: ICloudService) : ICloudProcess {

    private var process: Process? = null

    override fun start() {
        Launcher.instance.consoleSender.sendMessage("wrapper.service.starting", "Starting service %NAME%", cloudService.getName(), ".")
        cloudService.setState(ServiceState.STARTING)
        CloudLib.instance.getCloudServiceManger().updateCloudService(cloudService)
        val serviceTmpDir = if (cloudService.isStatic()) File(DirectoryPaths.paths.staticPath + cloudService.getName()) else File(DirectoryPaths.paths.tempPath + cloudService.getName())
        if (!cloudService.isStatic() || !serviceTmpDir.exists())
            TemplateCopier().copyTemplate(cloudService, cloudService.getTemplate())
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
                    //TODO send message to manager
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Launcher.instance.consoleSender.sendMessage("wrapper.service.stopped", "Service %NAME%", cloudService.getName(), " was stopped.")
    }

    override fun forceStop() {
        process?.destroyForcibly()
    }

    override fun isActive(): Boolean = this.process?.isAlive ?: false

    override fun shutdown() {
    }


    override fun getCloudService(): ICloudService = cloudService

}