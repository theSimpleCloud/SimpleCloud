package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.launcher.external.module.CloudModuleLoader
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class ApplicationStarter {

    fun startApplication(applicationType: CloudApplicationType){
        Launcher.instance.logger.applicationName = applicationType.getApplicationName()
        val file = File("SimpleCloud.jar")
        val cloudModule = CloudModuleLoader().loadModule(file, applicationType.name.toLowerCase() + ".json").cloudModule
        cloudModule as ICloudApplication
        Launcher.instance.activeApplication = cloudModule
    }


}