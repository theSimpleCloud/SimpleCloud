package eu.thesimplecloud.api.service

import eu.thesimplecloud.api.cachelist.ICacheList
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICloudServiceManager : ICacheList<ICloudService> {

    /**
     * Removes the [ICloudService] found by the specified name
     */
    fun deleteCloudService(name: String)

    /**
     * Returns the [ICloudService] found by the specified name
     */
    fun getCloudServiceByName(name: String): ICloudService? = getAllCachedObjects().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns a list of all registered services found by this group name
     */
    fun getCloudServicesByGroupName(groupName: String): List<ICloudService> = getAllCachedObjects().filter { it.getGroupName().equals(groupName, true) }.sortedBy { it.getServiceNumber() }

    /**
     * Returns a list of services found by the specified group name which are in LOBBY state
     */
    fun getCloudServicesInLobbyStateByGroupName(groupName: String): List<ICloudService> = getCloudServicesByGroupName(groupName).filter { it.getState() == ServiceState.VISIBLE }

    /**
     * Returns a list of services found by the specified group name which are in LOBBY state and are not full
     */
    fun getNotFullServicesInLobbyStateByGroupName(groupName: String): List<ICloudService> = getCloudServicesInLobbyStateByGroupName(groupName).filter { it.getOnlineCount() < it.getMaxPlayers() }

    /**
     * Returns a list of all services running on the specified wrapper
     */
    fun getServicesRunningOnWrapper(wrapperName: String): List<ICloudService> = getAllCachedObjects().filter { it.getWrapperName().equals(wrapperName, true) }

    /**
     * Starts the specified service
     * @return a promise that completes  when the service connects to the manager
     */
    fun startService(cloudService: ICloudService): ICommunicationPromise<Unit> = throw UnsupportedOperationException("Can not start a service here.")

    /**
     * Stops the specified service
     * @return a promise that completes when the service was stopped.
     */
    fun stopService(cloudService: ICloudService): ICommunicationPromise<Unit>
}