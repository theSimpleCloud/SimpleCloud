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

package eu.thesimplecloud.api.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.connection.IPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.jsonlib.JsonLibExclude
import java.util.*

class CloudPlayer(
        name: String,
        uniqueId: UUID,
        firstLogin: Long,
        lastLogin: Long,
        onlineTime: Long,
        private var connectedProxyName: String,
        private var connectedServerName: String?,
        playerConnection: DefaultPlayerConnection,
        propertyMap: MutableMap<String, Property<*>>
) : OfflineCloudPlayer(
        name,
        uniqueId,
        firstLogin,
        lastLogin,
        onlineTime,
        playerConnection,
        propertyMap
), ICloudPlayer {

    @Volatile
    private var connectState: PlayerServerConnectState = PlayerServerConnectState.CONNECTING

    @Volatile
    private var online = true

    @JsonIgnore
    @PacketExclude
    @Volatile
    private var updatesEnabled = false

    @JsonIgnore
    @PacketExclude
    @JsonLibExclude
    private var playerMessageQueue: PlayerMessageQueue? = null

    override fun getPlayerConnection(): IPlayerConnection = this.playerConnection

    override fun getServerConnectState(): PlayerServerConnectState {
        return this.connectState
    }

    fun setServerConnectState(connectState: PlayerServerConnectState) {
        this.connectState = connectState
    }

    @Synchronized
    override fun sendMessage(cloudText: CloudText): ICommunicationPromise<Unit> {
        if (playerMessageQueue == null) playerMessageQueue = PlayerMessageQueue(this)
        return this.playerMessageQueue!!.queueMessage(cloudText)
    }

    override fun getConnectedProxyName(): String = this.connectedProxyName

    override fun getConnectedServerName(): String? = this.connectedServerName

    override fun toOfflinePlayer(): IOfflineCloudPlayer = OfflineCloudPlayer(getName(), getUniqueId(), getFirstLogin(), getLastLogin(), getOnlineTime(), this.playerConnection, this.propertyMap)

    override fun isOnline(): Boolean = this.online

    override fun clone(): ICloudPlayer = CloudPlayer(getName(), getUniqueId(), getFirstLogin(), getLastLogin(), getOnlineTime(), connectedProxyName, connectedServerName, this.playerConnection, propertyMap)

    override fun enableUpdates() {
        super.enableUpdates()
        this.updatesEnabled = true
    }

    override fun disableUpdates() {
        super.disableUpdates()
        this.updatesEnabled = false
    }

    override fun isUpdatesEnabled(): Boolean {
        return this.updatesEnabled
    }

    @Synchronized
    fun setOffline() {
        this.online = false
    }

    fun setConnectedProxyName(name: String) {
        this.connectedProxyName = name
    }

    fun setConnectedServerName(name: String?) {
        this.connectedServerName = name
    }


    override fun getOnlineTime(): Long {
        return (System.currentTimeMillis() - getLastLogin()) + super.getOnlineTime()
    }

    override fun toString(): String {
        return JsonLib.fromObject(this).getAsJsonString()
    }
}