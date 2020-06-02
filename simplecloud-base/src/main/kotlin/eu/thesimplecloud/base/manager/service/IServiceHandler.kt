package eu.thesimplecloud.base.manager.service

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.startconfiguration.IServiceStartConfiguration
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup

interface IServiceHandler {

    /**
     * Starts the specified [count] of services and returns them in a list
     * @return all new services.
     */
    fun startServicesByGroup(cloudServiceGroup: ICloudServiceGroup, count: Int = 1): List<ICloudService>

    /**
     * Starts the specified service.
     * @param startConfiguration the configuration with the information to start the new service.
     * @return the new service
     */
    fun startService(startConfiguration: IServiceStartConfiguration): ICloudService
}