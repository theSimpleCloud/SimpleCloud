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

package eu.thesimplecloud.api.service

import eu.thesimplecloud.api.cachelist.ICacheList
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICloudServiceManager : ICacheList<ICloudServiceUpdater, ICloudService> {

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

    /**
     * Copies the service to the template directory.
     * @return q promise that completes when the service was copied.
     */
    fun copyService(cloudService: ICloudService, path: String): ICommunicationPromise<Unit>
}