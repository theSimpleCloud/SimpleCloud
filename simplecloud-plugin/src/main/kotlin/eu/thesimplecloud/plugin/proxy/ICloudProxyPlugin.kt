package eu.thesimplecloud.plugin.proxy

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.plugin.ICloudServicePlugin

interface ICloudProxyPlugin : ICloudServicePlugin {

    fun addServiceToProxy(cloudService: ICloudService)

}