package eu.thesimplecloud.lib.service.impl

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.wrapper.IWritableWrapperInfo

class DefaultCloudServiceManager : ICloudServiceManager {

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

    override fun removeCloudService(cloudService: ICloudService) {
        this.services.remove(cloudService)
    }

    override fun getAllCloudServices(): List<ICloudService> = this.services
}