package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.launcher.config.LauncherConfigLoader
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.IpValidator

class IpSetup : ISetup {


    @SetupQuestion(0, "manager.setup.ip.question", "Please enter the ip of the manager")
    fun setup(string: String): Boolean {
        if (!IpValidator().validate(string)) {
            Launcher.instance.consoleSender.sendMessage("launcher.setup.ip.ip-invalid", "The entered ip is invalid.")
            return false
        }
        val launcherConfig = LauncherConfigLoader().loadConfig()
        launcherConfig.host = string
        LauncherConfigLoader().saveConfig(launcherConfig)
        return true
    }

}