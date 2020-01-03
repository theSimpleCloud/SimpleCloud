package eu.thesimplecloud.api.service

interface ICloudServiceManager {

    /**
     * Updates or adds a [ICloudService]
     */
    fun updateCloudService(cloudService: ICloudService)

    /**
     * Removes the [ICloudService] found by the specified name
     */
    fun removeCloudService(name: String)

    /**
     * Returns all registered [ICloudService]s
     */
    fun getAllCloudServices(): Collection<ICloudService>

    /**
     * Returns the [ICloudService] found by the specified name
     */
    fun getCloudServiceByName(name: String): ICloudService? = getAllCloudServices().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns a list of all registered services found by this group name
     */
    fun getCloudServicesByGroupName(groupName: String): List<ICloudService> = getAllCloudServices().filter { it.getGroupName().equals(groupName, true) }.sortedBy { it.getServiceNumber() }

    /**
     * Returns a list of services found by the specified group name which are in LOBBY state
     */
    fun getCloudServicesInLobbyStateByGroupName(groupName: String): List<ICloudService> = getCloudServicesByGroupName(groupName).filter { it.getState() == ServiceState.VISIBLE }

    /**
     * Returns a list of services found by the specified group name which are in LOBBY state and are not full
     */
    fun getNotFullServicesInLobbyStateByGroupName(groupName: String): List<ICloudService> = getCloudServicesInLobbyStateByGroupName(groupName).filter { it.getOnlinePlayers() < it.getMaxPlayers() }

    /**
     * Returns a list of all services running on the specified wrapper
     */
    fun getServicesRunningOnWrapper(wrapperName: String): List<ICloudService> = getAllCloudServices().filter { it.getWrapperName().equals(wrapperName, true) }

    /**
     * Starts the specified service
     */
    fun startService(cloudService: ICloudService): Unit = throw UnsupportedOperationException("Can not start a service here.")

    /**
     * Stops the specified service
     */
    fun stopService(cloudService: ICloudService)

    /**
     * Updates the specified cloud service to the network
     */
    fun updateToNetwork(cloudService: ICloudService)
}