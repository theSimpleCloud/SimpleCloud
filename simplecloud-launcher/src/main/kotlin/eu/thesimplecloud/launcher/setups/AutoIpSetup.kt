package eu.thesimplecloud.launcher.setups

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.launcher.config.LauncherConfigLoader
import eu.thesimplecloud.launcher.console.setup.ISetup
import eu.thesimplecloud.launcher.console.setup.annotations.SetupQuestion
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.launcher.utils.IpValidator
import eu.thesimplecloud.launcher.utils.WebsiteContentLoader

class AutoIpSetup : ISetup {

    @SetupQuestion(0, "manager.setup.auto-ip.question", "Do you want to automatically set up your ip via \"ipify.org\" (yes / no)")
    fun setup(boolean: Boolean): Boolean {
        if (!boolean) {
            Launcher.instance.setupManager.queueSetup(IpSetup(), true)
            return true
        }

        val ip = try {
            val data = WebsiteContentLoader().loadContent("https://api.ipify.org?format=json")
            JsonData.fromJsonString(data).getString("ip")
        } catch (e: Exception) {
            Launcher.instance.logger.warning("Unable to connect to \"ipify.org\".")
            return false
        }
        if (ip == null || !IpValidator().validate(ip)) {
            Launcher.instance.logger.warning("Received response can not be parsed to an ip.")
            return false
        }
        Launcher.instance.consoleSender.sendMessage("launcher.setup.auto-ip.ip-fetched", "Ip fetched: %IP%", ip)
        val launcherConfig = LauncherConfigLoader().loadConfig()
        launcherConfig.host = ip
        LauncherConfigLoader().saveConfig(launcherConfig)
        return true
    }

}