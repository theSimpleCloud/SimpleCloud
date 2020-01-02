package eu.thesimplecloud.plugin.listener

import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.events.service.CloudServiceRegisteredEvent
import eu.thesimplecloud.api.events.service.CloudServiceUpdatedEvent
import eu.thesimplecloud.plugin.startup.CloudPlugin
import eu.thesimplecloud.plugin.proxy.ICloudProxyPlugin

class CloudServiceUpdateListener: IListener {

    @CloudEventHandler
    fun on(serviceEvent: CloudServiceUpdatedEvent) {
        val cloudServicePlugin = CloudPlugin.instance.cloudServicePlugin
        if (cloudServicePlugin is ICloudProxyPlugin){
            cloudServicePlugin.addServiceToProxy(serviceEvent.cloudService)
        }
    }

    @CloudEventHandler
    fun on(serviceEvent: CloudServiceRegisteredEvent) {
        val cloudServicePlugin = CloudPlugin.instance.cloudServicePlugin
        if (cloudServicePlugin is ICloudProxyPlugin){
            cloudServicePlugin.addServiceToProxy(serviceEvent.cloudService)
        }
    }


}