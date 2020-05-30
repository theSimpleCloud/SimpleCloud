package eu.thesimplecloud.module.hubcommand

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.startup.Launcher

class HubCommandModule : ICloudModule {

    override fun onEnable() {
        Launcher.instance.commandManager.registerCommand(this, HubCommand())
    }

    override fun onDisable() {
    }
}