package eu.thesimplecloud.plugin.listener

import eu.thesimplecloud.lib.eventapi.CloudEventHandler
import eu.thesimplecloud.lib.eventapi.IListener
import eu.thesimplecloud.lib.events.service.CloudServiceUpdatedEvent
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


}