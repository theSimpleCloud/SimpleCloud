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

package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.player.AbstractCloudPlayerManager
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.SimpleCloudPlayer
import eu.thesimplecloud.api.player.connection.ConnectionResponse
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import net.kyori.adventure.text.Component
import java.util.*

class CloudPlayerManagerImpl : AbstractCloudPlayerManager() {

    override fun getCloudPlayer(uniqueId: UUID): ICommunicationPromise<ICloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getCloudPlayer(name: String): ICommunicationPromise<ICloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, component: Component): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun connectPlayer(
        cloudPlayer: ICloudPlayer,
        cloudService: ICloudService
    ): ICommunicationPromise<ConnectionResponse> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendTitle(
        cloudPlayer: ICloudPlayer,
        title: String,
        subTitle: String,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int
    ) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendTablist(cloudPlayer: ICloudPlayer, headers: Array<String>, footers: Array<String>) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun setUpdates(cloudPlayer: ICloudPlayer, update: Boolean, serviceName: String) {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: SimpleLocation): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getLocationOfPlayer(cloudPlayer: ICloudPlayer): ICommunicationPromise<ServiceLocation> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getOfflineCloudPlayer(name: String): ICommunicationPromise<IOfflineCloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getOfflineCloudPlayer(uniqueId: UUID): ICommunicationPromise<IOfflineCloudPlayer> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getAllOnlinePlayers(): ICommunicationPromise<List<SimpleCloudPlayer>> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getNetworkOnlinePlayerCount(): ICommunicationPromise<Int> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getRegisteredPlayerCount(): ICommunicationPromise<Int> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun savePlayerToDatabase(offlinePlayer: IOfflineCloudPlayer): ICommunicationPromise<Unit> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }

    override fun getPlayersConnectedToService(cloudService: ICloudService): ICommunicationPromise<List<SimpleCloudPlayer>> {
        throw UnsupportedOperationException("Players are not supported in the wrapper")
    }
}