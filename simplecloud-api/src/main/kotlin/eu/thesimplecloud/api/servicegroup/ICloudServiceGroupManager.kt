package eu.thesimplecloud.api.servicegroup

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultServerGroup
import kotlin.NoSuchElementException

interface ICloudServiceGroupManager {

    /**
     * Updates or adds a [ICloudServiceGroup]
     */
    fun updateGroup(cloudServiceGroup: ICloudServiceGroup, fromPacket: Boolean = false)


    /**
     * Creates a new [ICloudServerGroup] by the specified parameters and returns a promise that is called when the group is registered
     */
    fun createServerGroup(
            groupName: String,
            templateName: String,
            memory: Int,
            maxPlayers: Int,
            minimumOnlineServiceCount: Int,
            maximumOnlineServiceCount: Int,
            maintenance: Boolean,
            static: Boolean,
            percentToStartNewService: Int,
            wrapperName: String?,
            serviceVersion: ServiceVersion,
            startPriority: Int,
            hiddenAtProxyGroups: List<String> = emptyList()
    ): ICommunicationPromise<ICloudServerGroup> =
            createServiceGroup(DefaultServerGroup(
                    groupName,
                    templateName,
                    memory,
                    maxPlayers,
                    minimumOnlineServiceCount,
                    maximumOnlineServiceCount,
                    maintenance,
                    static,
                    percentToStartNewService,
                    wrapperName,
                    serviceVersion,
                    startPriority,
                    hiddenAtProxyGroups
            )) as ICommunicationPromise<ICloudServerGroup>

    /**
     * Creates a new [ICloudLobbyGroup] by the specified parameters and returns a promise that is called when the group is registered
     */
    fun createLobbyGroup(
            groupName: String,
            templateName: String,
            memory: Int,
            maxPlayers: Int,
            minimumOnlineServiceCount: Int,
            maximumOnlineServiceCount: Int,
            maintenance: Boolean,
            static: Boolean,
            percentToStartNewService: Int,
            wrapperName: String?,
            priority: Int,
            permission: String?,
            serviceVersion: ServiceVersion,
            startPriority: Int,
            hiddenAtProxyGroups: List<String> = emptyList()
    ): ICommunicationPromise<ICloudLobbyGroup> =
            createServiceGroup(DefaultLobbyGroup(
                    groupName,
                    templateName,
                    memory,
                    maxPlayers,
                    minimumOnlineServiceCount,
                    maximumOnlineServiceCount,
                    maintenance,
                    static,
                    percentToStartNewService,
                    wrapperName,
                    priority,
                    permission,
                    serviceVersion,
                    startPriority,
                    hiddenAtProxyGroups
            )) as ICommunicationPromise<ICloudLobbyGroup>

    /**
     * Creates a new [ICloudProxyGroup] by the specified parameters and returns a promise that is called when the group is registered
     */
    fun createProxyGroup(
            groupName: String,
            templateName: String,
            memory: Int,
            maxPlayers: Int,
            minimumOnlineServiceCount: Int,
            maximumOnlineServiceCount: Int,
            maintenance: Boolean,
            static: Boolean,
            percentToStartNewService: Int,
            wrapperName: String,
            startPort: Int,
            serviceVersion: ServiceVersion,
            startPriority: Int
    ): ICommunicationPromise<ICloudProxyGroup> =
            createServiceGroup(DefaultProxyGroup(
                    groupName,
                    templateName,
                    memory,
                    maxPlayers,
                    minimumOnlineServiceCount,
                    maximumOnlineServiceCount,
                    maintenance,
                    static,
                    percentToStartNewService,
                    wrapperName,
                    startPort,
                    serviceVersion,
                    startPriority
            )) as ICommunicationPromise<ICloudProxyGroup>

    /**
     * Creates a service group and returns a promise that is called when the group is registered
     * [ICommunicationPromise.isSuccess] indicates whether the creation was successful
     * The promise will fail with:
     * - [IllegalArgumentException] if the group is already registered.
     */
    fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit>

    /**
     * Returns a list of all registered [ICloudServiceGroup]
     */
    fun getAllGroups(): Collection<ICloudServiceGroup>

    /**
     * Returns the [ICloudServiceGroup] found by the specified name
     */
    fun getServiceGroupByName(name: String): ICloudServiceGroup? = getAllGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [ICloudServerGroup] found by the specified name
     */
    fun getServerGroupByName(name: String): ICloudServerGroup? = getServerGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [ICloudLobbyGroup] found by the specified name
     */
    fun getLobbyGroupByName(name: String): ICloudLobbyGroup? = getLobbyGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [ICloudProxyGroup] found by the specified name
     */
    fun getProxyGroupByName(name: String): ICloudProxyGroup? = getProxyGroups().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns all registered proxy groups
     */
    fun getProxyGroups(): List<ICloudProxyGroup> = getAllGroups().filter { it.getServiceType() == ServiceType.PROXY }.map { it as ICloudProxyGroup }

    /**
     * Returns all registered lobby groups
     */
    fun getLobbyGroups(): List<ICloudLobbyGroup> = getAllGroups().filter { it.getServiceType() == ServiceType.LOBBY }.map { it as ICloudLobbyGroup }

    /**
     * Returns all registered server groups
     */
    fun getServerGroups(): List<ICloudServerGroup> = getAllGroups().filter { it.getServiceType() == ServiceType.SERVER }.map { it as ICloudServerGroup }

    /**
     * Clears the cache of all [ICloudServiceGroup]s
     */
    fun clearCache()

    /**
     * Starts a new service by the specified group
     * @return a promise that is completed when the service was registered with its name, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchElementException] if the specified group is not registered.
     */
    fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService>

    /**
     * Deletes the specified [cloudServiceGroup] group from the cloud.
     * @return a promise that will be completed when the deletion is done or an error occurs. [ICommunicationPromise.isSuccess] indicates whether the deletion was successful.
     * The promise will fail with
     * - [IllegalStateException] if services of the specified group are still registered.
     */
    fun deleteServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<Unit>

}