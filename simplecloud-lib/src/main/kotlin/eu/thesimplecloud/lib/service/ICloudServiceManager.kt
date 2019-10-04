package eu.thesimplecloud.lib.service

interface ICloudServiceManager {

    /**
     * Updates or adds a [ICloudService]
     */
    fun updateCloudService(cloudService: ICloudService)

    /**
     * Removes the specified [ICloudService]
     */
    fun removeCloudService(cloudService: ICloudService)

    /**
     * Returns the [ICloudService] found by the specified name
     */
    fun getCloudService(name: String): ICloudService

    /**
     * Returns a list of all registered services found by this group name
     */
    fun getCloudServicesByGroupName(groupName: String): List<ICloudService>

    /**
     * Returns a list of services found by the specified group name which are in LOBBY state
     */
    fun getCloudServicesInLobbyStateByGroupName(groupName: String): List<ICloudService>

    /**
     * Returns a list of services found by the specified group name which are in LOBBY state and are not full
     */
    fun getNotFullServicesInLobbyStateByGroupName(groupName: String): List<ICloudService>

    /**
     * Returns a list of all services running on the specified wrapper
     */
    fun getServicesRunningOnWrapper(wrapperName: String): List<ICloudService>
}