/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
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

package eu.thesimplecloud.api.service.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.AbstractCacheValueUpdater
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.api.service.ICloudServiceUpdater
import eu.thesimplecloud.api.service.ServiceState
import eu.thesimplecloud.api.utils.time.Timestamp
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * Created by IntelliJ IDEA.
 * Date: 18.01.2021
 * Time: 17:22
 * @author Frederick Baier
 */
class DefaultCloudServiceUpdater(
    private val delegateService: ICloudService
) : AbstractCacheValueUpdater(), ICloudServiceUpdater {

    override fun getCloudService(): ICloudService {
        return this.delegateService
    }

    override fun getState(): ServiceState {
        return getChangedValue("serviceState") ?: delegateService.getState()
    }

    override fun setState(serviceState: ServiceState) {
        changes["serviceState"] = serviceState
    }

    override fun getOnlineCount(): Int {
        return getChangedValue("onlineCount") ?: delegateService.getOnlineCount()
    }

    override fun setOnlineCount(amount: Int) {
        changes["onlineCount"] = amount
    }

    override fun getLastPlayerUpdate(): Timestamp {
        return getChangedValue("lastPlayerUpdate") ?: delegateService.getLastPlayerUpdate()
    }

    override fun setLastPlayerUpdate(timeStamp: Timestamp) {
        changes["lastPlayerUpdate"] = timeStamp
    }

    override fun getMaxPlayers(): Int {
        return getChangedValue("maxPlayers") ?: delegateService.getMaxPlayers()
    }

    override fun setMaxPlayers(amount: Int) {
        changes["maxPlayers"] = amount
    }

    override fun getMOTD(): String {
        return getChangedValue("motd") ?: delegateService.getMOTD()
    }

    override fun getDisplayName(): String {
        return getChangedValue("displayname") ?: delegateService.getDisplayName()
    }

    override fun isAuthenticated(): Boolean {
        return getChangedValue("authenticated") ?: delegateService.isAuthenticated()
    }

    override fun setAuthenticated(authenticated: Boolean) {
        changes["authenticated"] = authenticated
    }

    override fun setMOTD(motd: String) {
        changes["motd"] = motd
    }

    override fun setDisplayName(displayname: String) {
        changes["displayname"] = displayname
    }

    override fun update(): ICommunicationPromise<Unit> {
        val promise = CloudAPI.instance.getCloudServiceManager().update(delegateService)
        reset()
        return promise
    }

    private fun reset() {
        this.changes.clear()
    }
}