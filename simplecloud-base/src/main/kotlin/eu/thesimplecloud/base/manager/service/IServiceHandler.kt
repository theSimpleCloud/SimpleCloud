package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.lib.service.ICloudService
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup

interface IServiceHandler {

    fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int = 1): List<ICloudService>

}