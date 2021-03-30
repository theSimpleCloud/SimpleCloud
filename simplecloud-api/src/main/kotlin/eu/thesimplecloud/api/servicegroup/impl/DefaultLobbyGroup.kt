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

package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroupUpdater
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.api.servicegroup.grouptype.updater.ICloudLobbyGroupUpdater
import eu.thesimplecloud.api.servicegroup.impl.updater.DefaultLobbyGroupUpdater
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.jsonlib.JsonLibExclude

class DefaultLobbyGroup(
    name: String,
    templateName: String,
    maxMemory: Int,
    maxPlayers: Int,
    minimumOnlineServiceCount: Int,
    maximumOnlineServiceCount: Int,
    maintenance: Boolean,
    static: Boolean,
    percentToStartNewService: Int,
    wrapperName: String?,
    @Volatile private var priority: Int,
    permission: String?,
    serviceVersion: ServiceVersion,
    startPriority: Int,
    hiddenAtProxyGroups: List<String> = emptyList()
) : DefaultServerGroup(
    name,
    templateName,
    maxMemory,
    maxPlayers,
    minimumOnlineServiceCount,
    maximumOnlineServiceCount,
    maintenance,
    static,
    percentToStartNewService,
    wrapperName,
    serviceVersion,
    startPriority,
    permission,
    hiddenAtProxyGroups
), ICloudLobbyGroup {

    @Volatile
    @JsonLibExclude
    @PacketExclude
    private var updater: DefaultLobbyGroupUpdater? = DefaultLobbyGroupUpdater(this)

    override fun getPriority(): Int = this.priority

    override fun setPriority(priority: Int) {
        getUpdater().setPriority(priority)
    }

    override fun getUpdater(): ICloudLobbyGroupUpdater {
        if (this.updater == null) {
            this.updater = DefaultLobbyGroupUpdater(this)
        }
        return this.updater!!
    }

    override fun applyValuesFromUpdater(updater: ICloudServiceGroupUpdater) {
        super.applyValuesFromUpdater(updater)
        updater as ICloudLobbyGroupUpdater
        this.priority = updater.getPriority()
    }

    override fun toString(): String {
        return JsonLib.fromObject(this).getAsJsonString()
    }
}