package eu.thesimplecloud.base.manager.listener

import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.launcher.event.module.ModuleLoadedEvent
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class ModuleEventListener: IListener {

    @CloudEventHandler
    fun handleLoad(event: ModuleLoadedEvent) {
        val module = event.module
        Launcher.instance.consoleSender.sendMessage("manager.module.loaded", "Loaded module %NAME%", module.fileContent.name, " by %AUTHOR%", module.fileContent.author)
    }

    @CloudEventHandler
    fun handleUnload(event: ModuleUnloadedEvent) {
        val module = event.module
        Launcher.instance.commandManager.unregisterCommands(module.cloudModule)
        Launcher.instance.consoleSender.sendMessage("manager.module.unload", "Unloaded module %NAME%", module.fileContent.name, " by %AUTHOR%", module.fileContent.author)
    }

}