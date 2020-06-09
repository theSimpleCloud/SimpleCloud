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

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.api.event.player.permission.CloudPlayerPermissionCheckEvent
import eu.thesimplecloud.api.event.player.permission.PermissionState
import eu.thesimplecloud.api.exception.*
import eu.thesimplecloud.api.location.ServiceLocation
import eu.thesimplecloud.api.location.SimpleLocation
import eu.thesimplecloud.api.player.connection.ConnectionResponse
import eu.thesimplecloud.api.player.connection.IPlayerConnection
import eu.thesimplecloud.api.player.text.CloudText
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface ICloudPlayer : IOfflineCloudPlayer, ICommandSender {

    /**
     * Returns the [IPlayerConnection] of this player.
     */
    fun getPlayerConnection(): IPlayerConnection

    /**
     * Returns the state of the state of the connection to a server.
     */
    fun getServerConnectState(): PlayerServerConnectState

    /**
     * Sends a message to this player.
     */
    fun sendMessage(cloudText: CloudText): ICommunicationPromise<Unit>

    /**
     * Sends a message to this player.
     */
    override fun sendMessage(message: String) = sendMessage(CloudText(message))

    /**
     * Sends this player to the specified [cloudService]
     * @param cloudService the service the player shall be sent to.
     * @return a promise that is completed when the connection is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchServiceException] if the proxy service the player is connected to is not reachable
     * - [IllegalArgumentException] if the specified [cloudService] is a proxy service.
     */
    fun connect(cloudService: ICloudService): ICommunicationPromise<ConnectionResponse> = CloudAPI.instance.getCloudPlayerManager().connectPlayer(this, cloudService)

    /**
     * Kicks this player form the network.
     */
    fun kick(message: String) = CloudAPI.instance.getCloudPlayerManager().kickPlayer(this, message)

    /**
     * Kicks this player from the network.
     */
    fun kick() = kick("§cYou were kicked from the network.")

    /**
     * Sends a title to this player.
     */
    fun sendTitle(title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) = CloudAPI.instance.getCloudPlayerManager().sendTitle(this, title, subTitle, fadeIn, stay, fadeOut)

    /**
     * Sends a action bar to this player
     */
    fun sendActionBar(actionbar: String) = CloudAPI.instance.getCloudPlayerManager().sendActionbar(this, actionbar)

    /**
     * Returns the name of the proxy the player is connected to.
     */
    fun getConnectedProxyName(): String

    /**
     * Returns the name of the server the player is connected to.
     */
    fun getConnectedServerName(): String?

    /**
     * Returns the proxy this player is connected to.
     */
    fun getConnectedProxy(): ICloudService? = CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(getConnectedProxyName())

    /**
     * Returns the server this player is connected to.
     */
    fun getConnectedServer(): ICloudService? = getConnectedServerName()?.let { CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(it) }

    /**
     * Tells the manager that this client wants to receive updates of this player.
     * Note, that the proxy and the server the player is connected to automatically receive updates of the player.
     */
    fun enableUpdates() = CloudAPI.instance.getCloudPlayerManager().setUpdates(this, true, CloudAPI.instance.getThisSidesName())

    /**
     * Tells the manager that this client no longer wants to receive updates of this player.
     */
    fun disableUpdates() = CloudAPI.instance.getCloudPlayerManager().setUpdates(this, false, CloudAPI.instance.getThisSidesName())

    /**
     * Lets this player executes the specified [command]
     */
    fun forceCommandExecution(command: String) = CloudAPI.instance.getCloudPlayerManager().forcePlayerCommandExecution(this, command)

    /**
     * Teleports this player to the specified [location].
     * @return a promise that is completed when the teleportation is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableComponentException] if the minecraft server the player is connected is not reachable
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     */
    fun teleport(location: SimpleLocation): ICommunicationPromise<Unit> = CloudAPI.instance.getCloudPlayerManager().teleportPlayer(this, location)

    /**
     * Teleports this player to the specified [location].
     * @return a promise that is completed when the teleportation is complete, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableComponentException] if the minecraft server the player is connected is not reachable.
     * - [NoSuchServiceException] if the service to connect the player to cannot be found.
     * - [NoSuchWorldException] if the world to teleport the player to does not exist or is not loaded.
     * - [IllegalStateException] if the player is not connected to a server.
     * - [IllegalArgumentException] if the service in [ServiceLocation.getService] is a proxy service.
     * - [NoSuchPlayerException] if the player cannot be found on the proxy or the server service.
     * - [PlayerConnectException] if the proxy was unable to connect the player to the service.
     */
    fun teleport(location: ServiceLocation): ICommunicationPromise<Unit> = CloudAPI.instance.getCloudPlayerManager().teleportPlayer(this, location)

    override fun hasPermission(permission: String): ICommunicationPromise<Boolean> {
        val event = CloudPlayerPermissionCheckEvent(this, permission, PermissionState.UNKNOWN)
        CloudAPI.instance.getEventManager().call(event)
        return if (event.state == PermissionState.UNKNOWN) {
            CloudAPI.instance.getCloudPlayerManager().hasPermission(this, permission)
        } else {
            CommunicationPromise.of(event.state.name.toBoolean())
        }
    }

    /**
     *
     * @return a promise that is completed when the [ServiceLocation] is available, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [UnreachableComponentException] if the player is not connected to a server or the server is not connected to the manager.
     */
    fun getLocation(): ICommunicationPromise<ServiceLocation> = CloudAPI.instance.getCloudPlayerManager().getLocationOfPlayer(this)

    /**
     * Sends this player to a lobby server
     * @return a promise that is completed when this player is connected to the lobby server, or
     * when an exception is encountered. [ICommunicationPromise.isSuccess] indicates success
     * or failure.
     * The promise will fail with:
     * - [NoSuchPlayerException] if the player cannot be found on the proxy.
     * - [UnreachableComponentException] if the proxy server the player is connected is not reachable.
     * - [NoSuchServiceException] if no lobby was available to send the player to.
     */
    fun sendToLobby(): ICommunicationPromise<Unit> = CloudAPI.instance.getCloudPlayerManager().sendPlayerToLobby(this)

    /**
     * Returns a new [IOfflineCloudPlayer] with the data of this player
     */
    fun toOfflinePlayer(): IOfflineCloudPlayer

    /**
     * Clones this player.
     */
    fun clone(): ICloudPlayer

    /**
     * Returns a new [SimpleCloudPlayer] by the data of this player.
     */
    fun toSimplePlayer(): SimpleCloudPlayer {
        return SimpleCloudPlayer(getName(), getUniqueId())
    }

    /**
     * Updates this player to the network
     */
    fun update() = CloudAPI.instance.getCloudPlayerManager().update(this)

    /**
     * Returns whether updates for this player are enables on this network component
     */
    fun isUpdatesEnabled(): Boolean

}