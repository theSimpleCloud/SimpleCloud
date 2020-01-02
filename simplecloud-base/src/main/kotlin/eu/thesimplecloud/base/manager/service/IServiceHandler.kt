package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

interface IServiceHandler {

    fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int = 1): List<ICloudService>

}