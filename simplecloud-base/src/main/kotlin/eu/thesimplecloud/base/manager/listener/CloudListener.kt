package eu.thesimplecloud.base.manager.listener

import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUpdatedEvent

class CloudListener : IListener {

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        Launcher.instance.consoleSender.sendMessage("manager.service.stopped", "Service %SERVICE%", event.cloudService.getName(), " was stopped.")
    }

}