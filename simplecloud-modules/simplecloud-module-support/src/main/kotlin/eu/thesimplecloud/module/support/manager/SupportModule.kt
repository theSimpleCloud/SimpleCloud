package eu.thesimplecloud.module.support.manager

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.support.manager.command.DumpCommand

/**
 * Created by MrManHD
 * Class create at 25.06.2023 21:43
 */

class SupportModule : ICloudModule {

    override fun onEnable() {
        Launcher.instance.commandManager.registerCommand(this, DumpCommand())
    }

    override fun onDisable() {

    }

}