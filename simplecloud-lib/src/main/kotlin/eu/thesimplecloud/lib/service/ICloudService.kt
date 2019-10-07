package eu.thesimplecloud.lib.service

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import eu.thesimplecloud.lib.utils.IAuthenticatable
import eu.thesimplecloud.lib.wrapper.IWrapperInfo
import java.lang.IllegalStateException
import java.util.*

interface ICloudService : IAuthenticatable, IBootstrap {

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
     * Returns the template that this service uses
     * e.g. Lobby
     */
    fun getTemplateName(): String

    /**
     * Returns the service group of this service
     */
    fun getServiceGroup(): ICloudServiceGroup = CloudLib.instance.getCloudServiceGroupManager().getGroup(getGroupName()) ?: throw IllegalStateException("Can't find the service group of an registered service.")

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
    fun getWrapper(): IWrapperInfo = CloudLib.instance.getWrapperManager().getWrapperByName(getWrapperName()) ?: throw IllegalStateException("Can't find the wrapper where the service ${getName()} is running on.")

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
    fun getName(): String = getGroupName() + "-" + getServiceNumber()

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

    override fun isActive(): Boolean = getState() != ServiceState.PREPARED && getState() != ServiceState.CLOSED

    fun isJoinable() = this == ServiceState.LOBBY || this == ServiceState.INGAME

}