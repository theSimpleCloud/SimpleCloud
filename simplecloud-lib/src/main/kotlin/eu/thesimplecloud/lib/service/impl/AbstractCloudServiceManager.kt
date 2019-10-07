package eu.thesimplecloud.lib.service.impl

import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.events.service.*
import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.service.ServiceState
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo

abstract class AbstractCloudServiceManager : ICloudServiceManager {

    private val services = ArrayList<ICloudService>()

    override fun updateCloudService(cloudService: ICloudService) {
        val cashedService = getCloudService(cloudService.getName())
        if (cashedService == null){
            this.services.add(cloudService)
            CloudLib.instance.getEventManager().call(CloudServiceRegisteredEvent(cloudService))
            return
        }
        val nowStarting = cashedService.getState() == ServiceState.PREPARED && cloudService.getState() == ServiceState.STARTING
        val nowOnline = cashedService.getState() == ServiceState.STARTING && cloudService.isJoinable()
        cashedService.setMOTD(cloudService.getMOTD())
        cashedService.setOnlinePlayers(cloudService.getOnlinePlayers())
        cashedService.setState(cloudService.getState())
        cashedService.setAuthenticated(cloudService.isAuthenticated())
        cashedService.setLastUpdate(System.currentTimeMillis())

        CloudLib.instance.getEventManager().call(CloudServiceUpdatedEvent(cashedService))
        if (nowStarting) CloudLib.instance.getEventManager().call(CloudServiceStartingEvent(cashedService))
        if (nowOnline) CloudLib.instance.getEventManager().call(CloudServiceStartedEvent(cashedService))
    }

    override fun removeCloudService(name: String) {
        getCloudService(name)?.let {
            this.services.remove(it)
            CloudLib.instance.getEventManager().call(CloudServiceUnregisteredEvent(it))
        }
    }

    override fun getAllCloudServices(): List<ICloudService> = this.services
}