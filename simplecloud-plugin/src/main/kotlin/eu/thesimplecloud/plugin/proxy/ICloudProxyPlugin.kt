package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.plugin.ICloudServicePlugin

interface ICloudProxyPlugin : ICloudServicePlugin {

    fun addServiceToProxy(cloudService: ICloudService)


    fun removeServiceFromProxy(cloudService: ICloudService)


}