package eu.thesimplecloud.lib.service.impl

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo

abstract class AbstractCloudServiceManager : ICloudServiceManager {

    private val services = ArrayList<ICloudService>()

    override fun updateCloudService(cloudService: ICloudService) {
        val cashedService = getCloudService(cloudService.getName())
        if (cashedService == null){
            this.services.add(cloudService)
            return
        }
        cashedService.setMOTD(cloudService.getMOTD())
        cashedService.setOnlinePlayers(cloudService.getOnlinePlayers())
        cashedService.setState(cloudService.getState())
        cashedService.setAuthenticated(cloudService.isAuthenticated())
    }

    override fun removeCloudService(name: String) {
        getCloudService(name)?.let { this.services.remove(it) }
    }

    override fun getAllCloudServices(): List<ICloudService> = this.services
}