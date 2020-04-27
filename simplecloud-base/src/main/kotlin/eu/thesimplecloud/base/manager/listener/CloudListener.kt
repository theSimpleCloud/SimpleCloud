package eu.thesimplecloud.base.manager.listener

import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.event.player.CloudPlayerUnregisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.event.module.ModuleUnloadedEvent
import eu.thesimplecloud.launcher.extension.sendMessage
import eu.thesimplecloud.launcher.startup.Launcher

class CloudListener : IListener {

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        Launcher.instance.consoleSender.sendMessage("manager.service.stopped", "Service %SERVICE%", event.cloudService.getName(), " was stopped.")
        val activeScreen = Launcher.instance.screenManager.getActiveScreen()
        activeScreen?.let {
            if (activeScreen.getName().equals(event.cloudService.getName(), true)) {
                Launcher.instance.screenManager.leaveActiveScreen()
            }
        }
        Launcher.instance.screenManager.unregisterScreen(event.cloudService.getName())
    }

    @CloudEventHandler
    fun on(event: ModuleUnloadedEvent) {
        Manager.instance.packetRegistry.unregisterAllPackets(event.module.cloudModule)
    }

    @CloudEventHandler
    fun on(event: CloudPlayerLoginEvent) {
        event.getCloudPlayer().then {
            Launcher.instance.consoleSender
                    .sendMessage("manager.player.connected", "Player %NAME%", it.getName(), "(%UUID% ", it.getUniqueId().toString(),
                            "/ %IP%", it.getPlayerConnection().getAddress().getHostname(), ") connected.")
        }

    }

    @CloudEventHandler
    fun on(event: CloudPlayerUnregisteredEvent) {
        val player = event.cloudPlayer

        Launcher.instance.consoleSender
                .sendMessage("manager.player.disconnected", "Player %NAME%", player.getName(), "(%UUID% ", player.getUniqueId().toString(),
                        "/ %IP%", player.getPlayerConnection().getAddress().getHostname(), ") disconnected.")

    }

}