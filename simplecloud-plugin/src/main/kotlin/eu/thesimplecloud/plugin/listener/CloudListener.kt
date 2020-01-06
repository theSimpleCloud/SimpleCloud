package eu.thesimplecloud.plugin.listener

import eu.thesimplecloud.api.event.player.CloudPlayerLoginEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.event.service.CloudServiceRegisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceUpdatedEvent
import eu.thesimplecloud.plugin.startup.CloudPlugin
import eu.thesimplecloud.plugin.proxy.ICloudProxyPlugin

class CloudListener : IListener {

    @CloudEventHandler
    fun on(serviceEvent: CloudServiceUpdatedEvent) {
        val cloudServicePlugin = CloudPlugin.instance.cloudServicePlugin
        if (cloudServicePlugin is ICloudProxyPlugin) {
            cloudServicePlugin.addServiceToProxy(serviceEvent.cloudService)
        }
    }

    @CloudEventHandler
    fun on(serviceEvent: CloudServiceRegisteredEvent) {
        val cloudServicePlugin = CloudPlugin.instance.cloudServicePlugin
        if (cloudServicePlugin is ICloudProxyPlugin) {
            cloudServicePlugin.addServiceToProxy(serviceEvent.cloudService)
        }
    }

    @CloudEventHandler
    fun on(serviceEvent: CloudServiceUnregisteredEvent) {
        val cloudServicePlugin = CloudPlugin.instance.cloudServicePlugin
        if (cloudServicePlugin is ICloudProxyPlugin) {
            cloudServicePlugin.removeServiceFromProxy(serviceEvent.cloudService)
        }
    }


}