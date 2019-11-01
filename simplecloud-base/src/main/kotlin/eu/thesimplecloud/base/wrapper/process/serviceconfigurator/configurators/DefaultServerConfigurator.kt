package eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators

import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.IServiceConfigurator
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.utils.FileEditor
import java.io.File

class DefaultServerConfigurator : IServiceConfigurator {

    override fun configureService(cloudService: ICloudService, serviceTmpDirectory: File) {
        val propertiesFile = File(serviceTmpDirectory, "server.properties")
        val bukkitFile = File(serviceTmpDirectory, "bukkit.yml")
        val spigotFile = File(serviceTmpDirectory, "spigot.yml")
        if (!propertiesFile.exists())
            copyFileOutOfJar(propertiesFile, "/files/server.properties")
        if (!bukkitFile.exists())
            copyFileOutOfJar(bukkitFile, "/files/bukkit.yml")
        if (!spigotFile.exists())
            copyFileOutOfJar(spigotFile, "/files/spigot.yml")
        val fileEditor = FileEditor(propertiesFile)
        fileEditor["server-ip"] = cloudService.getHost()
        fileEditor["server-port"] = cloudService.getPort().toString()
        fileEditor["max-players"] = cloudService.getMaxPlayers().toString()
        fileEditor.save()
    }
}