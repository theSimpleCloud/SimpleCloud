package eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.utils.FileEditor
import eu.thesimplecloud.base.core.utils.FileCopier
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.IServiceConfigurator
import java.io.File

class DefaultVelocityConfigurator : IServiceConfigurator {

    override fun configureService(cloudService: ICloudService, serviceTmpDirectory: File) {
        val configFile = File(serviceTmpDirectory, "velocity.toml")
        if (!configFile.exists()) {
            FileCopier.copyFileOutOfJar(configFile, "/files/velocity.toml")
        }
        val fileEditor = FileEditor(configFile)
        fileEditor.replaceLine("bind = \"0.0.0.0:25577\"", "bind = \"0.0.0.0:${cloudService.getPort()}\"")
        fileEditor.replaceLine("show-max-players = 500", "show-max-players = ${cloudService.getMaxPlayers()}")
        fileEditor.save(configFile)
    }


}