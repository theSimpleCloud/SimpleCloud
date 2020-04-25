package eu.thesimplecloud.module.cloudflare

import eu.thesimplecloud.api.event.service.CloudServiceConnectedEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 11.04.2020
 * Time: 20:09
 */
class CloudListener(val module: CloudFlareModule) : IListener {

    @CloudEventHandler
    fun on(event: CloudServiceConnectedEvent) {
        val service = event.cloudService
        if (!service.isProxy()) {
            return
        }

        module.cloudFlareAPI.createForService(service)
    }

    @CloudEventHandler
    fun on(event: CloudServiceUnregisteredEvent) {
        val service = event.cloudService
        if (!service.isProxy()) {
            return
        }

        module.cloudFlareAPI.deleteRecord(service)
    }

}