package eu.thesimplecloud.api.service.startconfiguration

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface IServiceStartConfiguration {

    /**
     * Returns the name of the group of the service to start.
     */
    fun getServiceGroupName(): String

    /**
     * Sets the maximum amount of players
     * @return this [IServiceStartConfiguration]
     */
    fun setMaxPlayers(maxPlayers: Int): IServiceStartConfiguration

    /**
     * Sets the maximum amount of memory
     * @param memory the amount of memory in MB
     * @return this [IServiceStartConfiguration]
     */
    fun setMaxMemory(memory: Int): IServiceStartConfiguration

    /**
     * Sets the template for the new service
     * @return this [IServiceStartConfiguration]
     */
    fun setTemplate(template: ITemplate): IServiceStartConfiguration

    /**
     * Sets the number of this service.
     * e.g: Lobby-2 -> 2 is the service number
     * @return this [IServiceStartConfiguration]
     */
    fun setServiceNumber(number: Int): IServiceStartConfiguration

    /**
     * Returns the group of the new service.
     */
    fun getServiceGroup(): ICloudServiceGroup {
        return CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(getServiceGroupName())
                ?: throw IllegalStateException("ServiceGroup by name ${getServiceGroupName()} is null")
    }

    /**
     * Starts the service
     * @return a promise that completes when the new service was registered
     */
    fun startService(): ICommunicationPromise<ICloudService> {
        return CloudAPI.instance.getCloudServiceGroupManager().startNewService(this)
    }

}