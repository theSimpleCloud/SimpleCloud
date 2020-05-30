package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate

interface IServiceHandler {

    /**
     * Starts the specified [count] of services and returns them in a list
     * @return all new services.
     */
    fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int = 1): List<ICloudService>

    /**
     * Starts the specified service.
     * @param cloudServiceGroup the group of the service to start
     * @param template the template the service shall use
     * @param serviceNumber the number of the service e.g Lobby-2 -> 2 is the serviceNumber
     * @param memory the amount of memory the server shall use in MB
     */
    fun startService(cloudServiceGroup: ICloudServiceGroup, template: ITemplate, serviceNumber: Int, memory: Int): ICloudService
}