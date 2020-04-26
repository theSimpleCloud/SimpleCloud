package eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.FileEditor
import eu.thesimplecloud.base.core.utils.FileCopier
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.IServiceConfigurator
import java.io.File

class DefaultServerConfigurator : IServiceConfigurator {

    override fun configureService(cloudService: ICloudService, serviceTmpDirectory: File) {
        val propertiesFile = File(serviceTmpDirectory, "server.properties")
        val bukkitFile = File(serviceTmpDirectory, "bukkit.yml")
        val spigotFile = File(serviceTmpDirectory, "spigot.yml")
        if (!propertiesFile.exists())
            FileCopier.copyFileOutOfJar(propertiesFile, "/files/server.properties")
        if (!bukkitFile.exists())
            FileCopier.copyFileOutOfJar(bukkitFile, "/files/bukkit.yml")
        if (!spigotFile.exists())
            FileCopier.copyFileOutOfJar(spigotFile, "/files/spigot.yml")
        val fileEditor = FileEditor(propertiesFile)
        fileEditor["server-ip"] = cloudService.getHost()
        fileEditor["server-port"] = cloudService.getPort().toString()
        fileEditor["max-players"] = cloudService.getMaxPlayers().toString()
        fileEditor.save(propertiesFile)
    }
}