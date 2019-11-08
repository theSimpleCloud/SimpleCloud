package eu.thesimplecloud.lib.service

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.screen.ICommandExecutable
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.utils.IAuthenticatable
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import java.lang.IllegalStateException
import java.util.*
import java.util.function.Consumer

interface ICloudService : IAuthenticatable, IBootstrap, ICommandExecutable {

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
    fun getTemplate(): ITemplate = CloudLib.instance.getTemplateManager().getTemplate(getTemplateName()) ?: throw IllegalStateException("Can't find the template of an registered service")

    /**
     * Returns the service group of this service
     */
    fun getServiceGroup(): ICloudServiceGroup = CloudLib.instance.getCloudServiceGroupManager().getServiceGroup(getGroupName()) ?: throw IllegalStateException("Can't find the service group of an registered service")

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
    fun getWrapper(): IWrapperInfo = CloudLib.instance.getWrapperManager().getWrapperByName(getWrapperName()) ?: throw IllegalStateException("Can't find the wrapper where the service ${getName()} is running on. Wrapper-Name: ${getWrapperName()}")

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
        return getOnlinePlayers().toDouble() / getMaxPlayers()
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
    fun getOnlinePlayers(): Int

    /**
     * Sets the amount of online players.
     */
    fun setOnlinePlayers(amount: Int)

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
     * Returns weather this service joinable for players
     */
    fun isJoinable() = getState() == ServiceState.LOBBY || getState() == ServiceState.INGAME

    /**
     * Returns the promise that will be called once the service is starting.
     */
    fun startingPromise(): ICommunicationPromise<Unit>

    /**
     * Returns the promise that will be called once the service is connected in to the manager.
     */
    fun connectedPromise(): ICommunicationPromise<Unit>

    /**
     * Returns the promise that will be called once the service is joinable. ([isJoinable] changed to true)
     */
    fun joinablePromise(): ICommunicationPromise<Unit>

    /**
     * Returns the promise that will be called once the service is closed.
     */
    fun closedPromise(): ICommunicationPromise<Unit>



    override fun isActive(): Boolean = getState() != ServiceState.PREPARED && getState() != ServiceState.CLOSED

    override fun start() = CloudLib.instance.getCloudServiceManger().startService(this)

    override fun shutdown() = CloudLib.instance.getCloudServiceManger().stopService(this)

}