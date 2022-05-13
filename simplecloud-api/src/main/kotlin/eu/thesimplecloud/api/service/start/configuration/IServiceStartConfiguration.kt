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

package eu.thesimplecloud.api.service.start.configuration

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.service.start.future.IServiceStartPromise
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate

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
     * @return a custom promise that completes when the new service was registered
     */
    fun startService(): IServiceStartPromise {
        return CloudAPI.instance.getCloudServiceGroupManager().startNewService(this)
    }

}