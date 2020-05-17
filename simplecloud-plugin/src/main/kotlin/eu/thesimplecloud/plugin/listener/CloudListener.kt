package eu.thesimplecloud.plugin.listener

import eu.thesimplecloud.api.event.service.CloudServiceRegisteredEvent
import eu.thesimplecloud.api.event.service.CloudServiceStartedEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.plugin.proxy.ICloudProxyPlugin
import eu.thesimplecloud.plugin.startup.CloudPlugin

class CloudListener : IListener {

    @CloudEventHandler
    fun on(serviceEvent: CloudServiceStartedEvent) {
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