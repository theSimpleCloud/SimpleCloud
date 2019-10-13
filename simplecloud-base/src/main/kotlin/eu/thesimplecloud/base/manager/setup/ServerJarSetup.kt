package eu.thesimplecloud.base.manager.setup

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.Downloader
import eu.thesimplecloud.lib.service.ServiceVersion
import java.io.File

class ServerJarSetup(val serverJar: File) : ISetup {

    private var spigotType: String = ""

    @SetupQuestion(0, "manager.setup.server-jar.type.question", "Which server version do you want to use? (Spigot, Paper)")
    fun setup(answer: String): Boolean {
        this.spigotType = answer.toUpperCase()
        if (!(spigotType == "SPIGOT" || spigotType == "PAPER")) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.server-jar.type.invalid", "The specified type is invalid.")
            return false
        }
        return true
    }

    @SetupQuestion(1, "manager.setup.server-jar.version.question", "Which version to you want to use? (1.7.10, 1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4)")
    fun versionSetup(answer: String) : Boolean {
        val version = answer.replace(".", "_")
        val serviceVersion = JsonData.fromObject(spigotType + "_" + version).getObjectOrNull(ServiceVersion::class.java)
        if (serviceVersion == null) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.server-jar.version.unsupported", "The specified version is not supported.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("manager.setup.server-jar.downloading", "Downloading server...")
        Downloader().userAgentDownload(serviceVersion.downloadLink, serverJar.path)
        return true
    }


}
