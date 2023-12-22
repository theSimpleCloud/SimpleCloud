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

package eu.thesimplecloud.api.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.cachelist.value.ICacheValue
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.event.service.CloudServiceConnectedEvent
import eu.thesimplecloud.api.event.service.CloudServiceStartedEvent
import eu.thesimplecloud.api.event.service.CloudServiceStartingEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.network.component.INetworkComponent
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.player.SimpleCloudPlayer
import eu.thesimplecloud.api.property.IPropertyMap
import eu.thesimplecloud.api.service.version.ServiceVersion
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import eu.thesimplecloud.clientserverapi.lib.promise.toListPromise
import java.util.*

interface ICloudService : ICacheValue<ICloudServiceUpdater>, ICloudServiceVariables, INetworkComponent, IBootstrap,
    IPropertyMap {

    /**
     * Returns the service group name of this service
     * e.g Lobby
     */
    fun getGroupName(): String

    /**
     * Returns the number of this service.
     * e.g. When the service name is Lobby-2 the service number will be 2
     */
    fun getServiceNumber(): Int

    /**
     * Returns the Unique Id of this service
     */
    fun getUniqueId(): UUID

    /**
     * Returns the type of this service
     */
    fun getServiceType(): ServiceType = getServiceGroup().getServiceType()

    /**
     * Returns the version, this service is running on
     */
    fun getServiceVersion(): ServiceVersion

    /**
     * Returns the name of the template that this service uses
     * e.g. Lobby
     */
    fun getTemplateName(): String

    /**
     * Returns the template that this service uses
     * e.g. Lobby
     */
    fun getTemplate(): ITemplate = CloudAPI.instance.getTemplateManager().getTemplateByName(getTemplateName())
        ?: throw IllegalStateException(
            "Can't find the template of an registered service (templates: ${
                CloudAPI.instance.getTemplateManager().getAllCachedObjects().joinToString { it.getName() }
            })"
        )

    /**
     * Returns the service group of this service
     */
    fun getServiceGroup(): ICloudServiceGroup =
        CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(getGroupName())
            ?: throw IllegalStateException("Can't find the service group of an registered service")

    /**
     * Returns the minimum amount of RAM for this service in MB
     */
    fun getMinimumMemory(): Int

    /**
     * Returns the maximum amount of RAM for this service in MB
     */
    fun getMaxMemory(): Int

    /**
     * Returns the used amount of RAM for this service in MB
     */
    fun getUsedMemory(): Int

    /**
     * Returns the name of the wrapper this service is running on
     */
    fun getWrapperName(): String?

    /**
     * Returns the wrapper this service is running on
     */
    fun getWrapper(): IWrapperInfo =
        getWrapperName()?.let { CloudAPI.instance.getWrapperManager().getWrapperByName(it) }
            ?: throw IllegalStateException("Can't find the wrapper where the service ${getName()} is running on. Wrapper-Name: ${getWrapperName()}")

    /**
     * Returns the host of this service
     */
    fun getHost(): String = getWrapper().getHost()

    /**
     * Returns the port this service is bound to
     */
    fun getPort(): Int

    /**
     * Returns whether this service is static.
     */
    fun isStatic(): Boolean = getServiceGroup().isStatic()

    /**
     * Returns whether this service has force copy templates enabled.
     */
    fun isForceCopyTemplates(): Boolean = getServiceGroup().isForceCopyTemplates()

    /**
     * Returns the percentage of occupied slots
     */
    fun getOnlinePercentage(): Double {
        return getOnlineCount().toDouble() / getMaxPlayers()
    }

    /**
     * Returns the name of this service.
     * e.g. Lobby-1
     */
    override fun getName(): String = getGroupName() + "-" + getServiceNumber()

    /**
     *  Returns a promise of players currently connected to this service
     */
    fun getOnlinePlayers(): ICommunicationPromise<List<SimpleCloudPlayer>> {
        return CloudAPI.instance.getCloudPlayerManager().getPlayersConnectedToService(this)
    }

    /**
     *  Returns a promise of players currently connected to this service
     *  If you just need the name or the uniqueId of the the online players use [getOnlinePlayers] instead
     *  because this function takes much longer
     */
    fun getOnlinePlayersDirect(): ICommunicationPromise<List<ICloudPlayer?>> {
        return getOnlinePlayers().then { simplePlayers -> simplePlayers.map { it.getCloudPlayer() }.toListPromise() }
            .flatten()
    }

    /**
     * Returns weather this service is joinable for players.
     */
    fun isOnline() = isServiceJoinable()

    /**
     * Returns whether this service is full.
     */
    fun isFull() = getOnlineCount() >= getMaxPlayers()

    override fun getNetworkComponentType(): NetworkComponentType = NetworkComponentType.SERVICE

    /**
     * Creates a promise that completes when the service is starting.
     */
    fun createStartingPromise(): ICommunicationPromise<CloudServiceStartingEvent> {
        if (isActive() || getState() == ServiceState.CLOSED)
            return CommunicationPromise.of(CloudServiceStartingEvent(this))
        return cloudListener<CloudServiceStartingEvent>()
            .addCondition { it.cloudService === this@ICloudService }
            .toPromise()
    }

    /**
     * Creates a promise that completes when the service is connected to the manager.
     */
    fun createConnectedPromise(): ICommunicationPromise<CloudServiceConnectedEvent> {
        if (isAuthenticated() || getState() == ServiceState.CLOSED)
            return CommunicationPromise.of(CloudServiceConnectedEvent(this))
        return cloudListener<CloudServiceConnectedEvent>()
            .addCondition { it.cloudService === this@ICloudService }
            .toPromise()
    }

    /**
     * Creates a promise that completes when the service is started.
     */
    fun createStartedPromise(): ICommunicationPromise<CloudServiceStartedEvent> {
        if (isOnline() || getState() == ServiceState.CLOSED)
            return CommunicationPromise.of(CloudServiceStartedEvent(this))
        return cloudListener<CloudServiceStartedEvent>()
            .addCondition { it.cloudService === this@ICloudService }
            .toPromise()
    }

    /**
     * Creates a promise that completes when the service is closed.
     */
    fun createClosedPromise(): ICommunicationPromise<CloudServiceUnregisteredEvent> {
        if (getState() == ServiceState.CLOSED)
            return CommunicationPromise.of(CloudServiceUnregisteredEvent(this))
        return cloudListener<CloudServiceUnregisteredEvent>()
            .addCondition { it.cloudService === this@ICloudService }
            .toPromise()
    }

    /**
     * Returns whether this service is a lobby service.
     */
    fun isLobby(): Boolean = getServiceType() == ServiceType.LOBBY

    /**
     * Returns whether this service is a proxy service.
     */
    fun isProxy(): Boolean = getServiceType() == ServiceType.PROXY

    /**
     * Updates this service to the network
     */
    fun update(): ICommunicationPromise<Unit> {
        return getUpdater().update()
    }

    /**
     * Copies the service to the template directory.
     * @return a promise that completes when the service was copied.
     */
    fun copy(path: String): ICommunicationPromise<Unit> {
        return CloudAPI.instance.getCloudServiceManager().copyService(this, path)
    }

    /**
     * Returns true when this service is starting or visible
     */
    fun isStartingOrVisible() = getState() == ServiceState.STARTING || getState() == ServiceState.VISIBLE

    override fun isActive(): Boolean = getState() != ServiceState.PREPARED && getState() != ServiceState.CLOSED

    override fun start() = CloudAPI.instance.getCloudServiceManager().startService(this)

    override fun shutdown() = CloudAPI.instance.getCloudServiceManager().stopService(this)

}