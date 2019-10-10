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

    @SetupQuestion("manager.setup.proxy-jar.type.question", "Which proxy version do you want to use (Spigot, Paper)")
    fun setup(answer: String): Boolean {
        this.spigotType = answer.toUpperCase()
        return spigotType == "SPIGOT" || spigotType == "PAPER"
    }

    @SetupQuestion("manager.setup.proxy-jar.version.question", "Which version to you want to use (1.7.10, 1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4)")
    fun versionSetup(answer: String){
        val version = answer.replace(".", "_")
        val serviceVersion = JsonData.fromObject(spigotType + "_" + version).getObject(ServiceVersion::class.java)
        if (serviceVersion == null) {
            Launcher.instance.consoleSender.sendMessage("manager.setup.proxy-jar.version.unsupported", "The specified version is not supported.")
            return
        }
        Downloader().userAgentDownload(serviceVersion.downloadLink, serverJar.path)
    }


}
