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

import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate

/**
 * Creates a new [ServiceStartConfiguration] with the default values of the specified service group
 */
class ServiceStartConfiguration(serviceGroup: ICloudServiceGroup) : IServiceStartConfiguration {

    /**
     * The name of the group
     */
    val groupName = serviceGroup.getName()

    /**
     * The memory amount in MB for the new service.
     */
    @Volatile var maxMemory = serviceGroup.getMaxMemory()
        private set

    /**
     * The maximum amount of players for the new service.
     */
    @Volatile var maxPlayers = serviceGroup.getMaxPlayers()
        private set

    /**
     * The template the new service shall use.
     */
    @Volatile var template = serviceGroup.getTemplateName()
        private set

    /**
     * The number of the new service.
     * e.g: Lobby-2 -> 2 is the service number
     */
    @Volatile var serviceNumber: Int? = null
        private set


    override fun getServiceGroupName(): String {
        return this.groupName
    }

    override fun setMaxMemory(memory: Int): ServiceStartConfiguration {
        require(memory >= 100) { "The specified memory must be at least 100" }
        this.maxMemory = memory
        return this
    }

    override fun setMaxPlayers(maxPlayers: Int): ServiceStartConfiguration {
        require(maxPlayers > 0) { "The specified amount of maxPlayers must be positive." }
        this.maxPlayers = maxPlayers
        return this
    }

    override fun setTemplate(template: ITemplate): ServiceStartConfiguration {
        this.template = template.getName()
        return this
    }

    override fun setServiceNumber(number: Int): ServiceStartConfiguration {
        require(number > 0) { "The specified number must be positive." }
        this.serviceNumber = number
        return this
    }

}