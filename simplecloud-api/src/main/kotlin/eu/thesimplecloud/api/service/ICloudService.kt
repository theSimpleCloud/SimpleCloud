package eu.thesimplecloud.api.service

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.client.NetworkComponentType
import eu.thesimplecloud.api.event.service.CloudServiceConnectedEvent
import eu.thesimplecloud.api.event.service.CloudServiceStartedEvent
import eu.thesimplecloud.api.event.service.CloudServiceStartingEvent
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.listenerextension.cloudListener
import eu.thesimplecloud.api.property.IPropertyMap
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.api.template.ITemplate
import eu.thesimplecloud.api.utils.INetworkComponent
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

interface ICloudService : INetworkComponent, IBootstrap, IPropertyMap {

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
    fun getServiceVersion(): ServiceVersion = getServiceGroup().getServiceVersion()

    /**
     * Returns the name of the template that this service uses
     * e.g. Lobby
     */
    fun getTemplateName(): String

    /**
     * Returns the template that this service uses
     * e.g. Lobby
     */
    fun getTemplate(): ITemplate = CloudAPI.instance.getTemplateManager().getTemplateByName(getTemplateName()) ?: throw IllegalStateException("Can't find the template of an registered service (templates: ${CloudAPI.instance.getTemplateManager().getAllCachedObjects().joinToString { it.getName() }})")

    /**
     * Returns the service group of this service
     */
    fun getServiceGroup(): ICloudServiceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(getGroupName()) ?: throw IllegalStateException("Can't find the service group of an registered service")

    /**
     * Returns the maximum amount of RAM for this service in MB
     */
    fun getMaxMemory(): Int

    /**
     * Returns the name of the wrapper this service is running on
     */
    fun getWrapperName(): String

    /**
     * Returns the wrapper this service is running on
     */
    fun getWrapper(): IWrapperInfo = CloudAPI.instance.getWrapperManager().getWrapperByName(getWrapperName()) ?: throw IllegalStateException("Can't find the wrapper where the service ${getName()} is running on. Wrapper-Name: ${getWrapperName()}")

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
     * Returns the last time stamp the service was updated.
     */
    fun getLastUpdate(): Long

    /**
     * Sets the last time stamp the service was updated.
     */
    fun setLastUpdate(timeStamp: Long)

    /**
     * Returns the state of this service.
     */
    fun getState(): ServiceState

    /**
     * Sets the state of this service
     */
    fun setState(serviceState: ServiceState)

    /**
     * Returns the amount of players that are currently on this service
     */
    @Deprecated("Use getOnlineCount instead", ReplaceWith("getOnlineCount()"))
    fun getOnlinePlayers(): Int = getOnlineCount()

    /**
     * Returns the amount of players that are currently on this service
     */
    fun getOnlineCount(): Int

    /**
     * Sets the amount of online players.
     */
    fun setOnlineCount(amount: Int)

    /**
     * Returns the maximum amount of players for this service
     */
    fun getMaxPlayers(): Int = getServiceGroup().getMaxPlayers()

    /**
     * Returns the MOTD of this service.
     */
    fun getMOTD(): String

    /**
     * Sets the MOTD of this service.
     */
    fun setMOTD(motd: String)

    /**
     * Returns weather this service joinable for players.
     */
    fun isOnline() = getState() == ServiceState.VISIBLE || getState() == ServiceState.INVISIBLE

    /**
     * Returns whether this service is full.
     */
    fun isFull() = getOnlineCount() >= getMaxPlayers()

    override fun getNetworkComponentType(): NetworkComponentType = NetworkComponentType.SERVICE

    /**
     * Adds a callback that wll be called when the server is starting.
     */
    fun addStartingCallback(callback: Runnable) {
        val thisService = this
        val listenerObj = object: IListener {

            @CloudEventHandler
            fun handle(event: CloudServiceStartingEvent) {
                if (event.cloudService === thisService) {
                    callback.run()
                }
            }

        }
        CloudAPI.instance.getEventManager().registerListener(CloudAPI.instance.getThisSidesCloudModule(), listenerObj)
    }

    /**
     * Adds a callback that wll be called when the server is connected to the manager.
     */
    fun createConnectedPromise(): ICommunicationPromise<Unit> {
        return cloudListener<CloudServiceConnectedEvent>()
                .addCondition { it.cloudService === this@ICloudService }
                .toPromise()
    }

    /**
     * Adds a callback that wll be called when the server is started.
     */
    fun createStartedPromise(): ICommunicationPromise<Unit> {
        return cloudListener<CloudServiceStartedEvent>()
                .addCondition { it.cloudService === this@ICloudService }
                .toPromise()
    }

    /**
     * Adds a callback that wll be called when the server is connected to the manager.
     */
    fun createClosedPromise(): ICommunicationPromise<Unit> {
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
    fun update() = CloudAPI.instance.getCloudServiceManager().update(this)

    override fun isActive(): Boolean = getState() != ServiceState.PREPARED && getState() != ServiceState.CLOSED

    override fun start() = CloudAPI.instance.getCloudServiceManager().startService(this)

    override fun shutdown() = CloudAPI.instance.getCloudServiceManager().stopService(this)

}