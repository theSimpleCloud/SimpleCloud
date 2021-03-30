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

package eu.thesimplecloud.api.player.impl

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.AbstractCacheValueUpdater
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.ICloudPlayerUpdater
import eu.thesimplecloud.api.player.PlayerServerConnectState
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class CloudPlayerUpdater(
    private val cloudPlayer: ICloudPlayer
) : AbstractCacheValueUpdater(), ICloudPlayerUpdater {

    override fun getCloudPlayer(): ICloudPlayer {
        return cloudPlayer
    }

    override fun setDisplayName(name: String) {
        changes["displayName"] = name
    }

    override fun getDisplayName(): String {
        return getChangedValue("displayName") ?: cloudPlayer.getDisplayName()
    }

    fun setConnectedProxyName(name: String) {
        changes["connectedProxy"] = name
    }

    override fun getConnectedProxyName(): String {
        return getChangedValue("connectedProxy") ?: cloudPlayer.getConnectedProxyName()
    }

    fun setConnectedServerName(name: String) {
        changes["connectedServer"] = name
    }

    override fun getServerConnectState(): PlayerServerConnectState {
        return getChangedValue("connectState") ?: cloudPlayer.getServerConnectState()
    }

    fun setServerConnectState(state: PlayerServerConnectState) {
        changes["connectState"] = state
    }

    override fun getConnectedServerName(): String? {
        return getChangedValue("connectedServer") ?: cloudPlayer.getConnectedServerName()
    }

    override fun update(): ICommunicationPromise<Unit> {
        return CloudAPI.instance.getCloudPlayerManager().update(cloudPlayer)
    }
}