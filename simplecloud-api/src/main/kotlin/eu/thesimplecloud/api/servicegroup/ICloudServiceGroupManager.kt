/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.servicegroup

import eu.thesimplecloud.api.cachelist.ICacheList
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ServiceType
import eu.thesimplecloud.api.service.startconfiguration.IServiceStartConfiguration
import eu.thesimplecloud.api.service.startconfiguration.ServiceStartConfiguration
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultLobbyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultProxyGroup
import eu.thesimplecloud.api.servicegroup.impl.DefaultServerGroup
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICloudServiceGroupManager : ICacheList<ICloudServiceGroup> {


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
    fun createServiceGroup(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudServiceGroup>

    /**
     * Returns the [ICloudServiceGroup] found by the specified name
     */
    fun getServiceGroupByName(name: String): ICloudServiceGroup? = getAllCachedObjects().firstOrNull { it.getName().equals(name, true) }

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
    fun getProxyGroups(): List<ICloudProxyGroup> = getAllCachedObjects().filter { it.getServiceType() == ServiceType.PROXY }.map { it as ICloudProxyGroup }

    /**
     * Returns all registered lobby groups
     */
    fun getLobbyGroups(): List<ICloudLobbyGroup> = getAllCachedObjects().filter { it.getServiceType() == ServiceType.LOBBY }.map { it as ICloudLobbyGroup }

    /**
     * Returns all registered server groups
     */
    fun getServerGroups(): List<ICloudServerGroup> = getAllCachedObjects().filter { it.getServiceType() == ServiceType.SERVER }.map { it as ICloudServerGroup }

    /**
     * Starts a new service by the specified group
     * @return a promise that is completed when the service was registered with its name, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchElementException] if the specified group is not registered.
     */
    fun startNewService(cloudServiceGroup: ICloudServiceGroup): ICommunicationPromise<ICloudService> {
        return startNewService(ServiceStartConfiguration(cloudServiceGroup))
    }

    /**
     * Starts a new service by the specified [serviceStartConfiguration]
     * @return a promise that is completed when the service was registered with its name, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [IllegalArgumentException] if the service to start is already running
     */
    fun startNewService(serviceStartConfiguration: IServiceStartConfiguration): ICommunicationPromise<ICloudService>

}