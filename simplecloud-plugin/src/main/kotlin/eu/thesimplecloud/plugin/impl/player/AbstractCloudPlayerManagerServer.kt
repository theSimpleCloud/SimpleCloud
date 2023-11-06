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

package eu.thesimplecloud.plugin.impl.player

import eu.thesimplecloud.api.exception.NoSuchServiceException
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.network.packets.player.*
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.connection.ConnectionResponse
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.network.packets.PacketOutTeleportOtherService
import eu.thesimplecloud.plugin.startup.CloudPlugin
import net.kyori.adventure.text.Component

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.05.2020
 * Time: 21:58
 */
abstract class AbstractCloudPlayerManagerServer : AbstractServiceCloudPlayerManager() {

    override fun sendMessageToPlayer(cloudPlayer: ICloudPlayer, component: Component): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketIOSendMessageToCloudPlayer(
                cloudPlayer,
                component
            )
        )
    }

    override fun connectPlayer(
        cloudPlayer: ICloudPlayer,
        cloudService: ICloudService
    ): ICommunicationPromise<ConnectionResponse> {
        return CloudPlugin.instance.connectionToManager.sendQuery(
            PacketIOConnectCloudPlayer(cloudPlayer, cloudService),
            500
        )
    }

    override fun kickPlayer(cloudPlayer: ICloudPlayer, message: String): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.connectionToManager.sendUnitQuery(PacketIOKickCloudPlayer(cloudPlayer, message))
    }

    override fun sendTitle(
        cloudPlayer: ICloudPlayer,
        title: String,
        subTitle: String,
        fadeIn: Int,
        stay: Int,
        fadeOut: Int
    ) {
        CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketIOSendTitleToCloudPlayer(
                cloudPlayer,
                title,
                subTitle,
                fadeIn,
                stay,
                fadeOut
            )
        )
    }

    override fun forcePlayerCommandExecution(cloudPlayer: ICloudPlayer, command: String) {
        CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketIOCloudPlayerForceCommandExecution(
                cloudPlayer,
                command
            )
        )
    }

    override fun sendActionbar(cloudPlayer: ICloudPlayer, actionbar: Component) {
        CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketIOSendActionbarToCloudPlayer(
                cloudPlayer,
                actionbar
            )
        )
    }

    override fun sendTablist(cloudPlayer: ICloudPlayer, headers: Array<String>, footers: Array<String>) {
        CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketIOSendTablistToPlayer(
                cloudPlayer.getUniqueId(),
                headers,
                footers
            )
        )
    }

    override fun teleportPlayer(cloudPlayer: ICloudPlayer, location: ServiceLocation): ICommunicationPromise<Unit> {
        if (location.getService() == null) return CommunicationPromise.failed(NoSuchServiceException("Service to connect the player to cannot be found."))
        return CloudPlugin.instance.connectionToManager.sendUnitQuery(
            PacketOutTeleportOtherService(cloudPlayer.getUniqueId(), location.serviceName, location as SimpleLocation),
            1000
        )
    }

    override fun hasPermission(cloudPlayer: ICloudPlayer, permission: String): ICommunicationPromise<Boolean> {
        return CloudPlugin.instance.connectionToManager.sendQuery(
            PacketIOPlayerHasPermission(
                cloudPlayer.getUniqueId(),
                permission
            ), 400
        )
    }

    override fun sendPlayerToLobby(cloudPlayer: ICloudPlayer): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.connectionToManager.sendQuery(PacketIOSendPlayerToLobby(cloudPlayer.getUniqueId()))
    }

}