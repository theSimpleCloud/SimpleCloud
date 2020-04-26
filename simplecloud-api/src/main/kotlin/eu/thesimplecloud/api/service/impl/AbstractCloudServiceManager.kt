package eu.thesimplecloud.api.service.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.service.*
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ICloudServiceManager
import eu.thesimplecloud.api.service.ServiceState
import java.util.concurrent.CopyOnWriteArrayList

abstract class AbstractCloudServiceManager : ICloudServiceManager {

    private val services = CopyOnWriteArrayList<ICloudService>()

    override fun updateCloudService(cloudService: ICloudService, fromPacket: Boolean) {
        val cashedService = getCloudServiceByName(cloudService.getName())
        if (cashedService == null){
            cloudService.setLastUpdate(System.currentTimeMillis())
            this.services.add(cloudService)
            CloudAPI.instance.getEventManager().call(CloudServiceRegisteredEvent(cloudService))
            CloudAPI.instance.getEventManager().call(CloudServiceUpdatedEvent(cloudService))
            return
        }
        val nowStarting = cashedService.getState() == ServiceState.PREPARED && cloudService.getState() == ServiceState.STARTING
        val nowOnline = !cashedService.isOnline() && cloudService.isOnline()
        val nowConnected = !cashedService.isAuthenticated() && cloudService.isAuthenticated()

        cashedService.setMOTD(cloudService.getMOTD())
        cashedService.setOnlineCount(cloudService.getOnlineCount())
        cashedService.setState(cloudService.getState())
        cashedService.setAuthenticated(cloudService.isAuthenticated())
        cashedService.setLastUpdate(System.currentTimeMillis())
        cashedService as DefaultCloudService
        cashedService.setWrapperName(cloudService.getWrapperName())
        cashedService.setPort(cloudService.getPort())
        cashedService.propertyMap = HashMap(cloudService.getProperties())

        CloudAPI.instance.getEventManager().call(CloudServiceUpdatedEvent(cashedService))

        if (nowStarting) {
            CloudAPI.instance.getEventManager().call(CloudServiceStartingEvent(cashedService))
        }
        if (nowConnected) {
            CloudAPI.instance.getEventManager().call(CloudServiceConnectedEvent(cashedService))
        }
        if (nowOnline) {
            CloudAPI.instance.getEventManager().call(CloudServiceStartedEvent(cashedService))
        }
    }

    override fun removeCloudService(name: String) {
        getCloudServiceByName(name)?.let {
            this.services.remove(it)
            CloudAPI.instance.getEventManager().call(CloudServiceUnregisteredEvent(it))
        }
    }

    override fun getAllCloudServices(): Collection<ICloudService> = this.services
}