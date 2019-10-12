package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.launcher.module.CloudModuleLoader
import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class ApplicationStarter {

    fun startApplication(applicationType: CloudApplicationType){
        val file = File("SimpleCloud.jar")
        val cloudModule = CloudModuleLoader().loadModule(file, applicationType.name.toLowerCase() + ".json")
        cloudModule as ICloudApplication
        Launcher.instance.logger.applicationName = applicationType.getApplicationName()
        Launcher.instance.activeApplication = cloudModule
    }


}