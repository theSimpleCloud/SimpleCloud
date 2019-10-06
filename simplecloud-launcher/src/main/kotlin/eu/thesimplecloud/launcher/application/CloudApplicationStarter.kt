package eu.thesimplecloud.launcher.application

import eu.thesimplecloud.launcher.startup.Launcher
import java.io.File

class CloudApplicationStarter {

    fun startCloudApplication(cloudApplicationType: CloudApplicationType) {
        val cloudApplication = ApplicationLoader().loadApplicationClass(File("SimpleCloud.jar"), cloudApplicationType)
        Launcher.instance.activeApplication = cloudApplication
        cloudApplication.start()
        when (cloudApplicationType) {
            CloudApplicationType.WRAPPER -> {
            }
            CloudApplicationType.MANAGER -> {
            }
        }

    }

}