package eu.thesimplecloud.lib.service

import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroup
import java.util.*

interface ICloudService {

    /**
     * Starts this service
     */
    fun start()

    /**
     * Stops this service
     */
    fun stop()

    /**
     * Returns the state of this service.
     */
    fun getState(): ServiceState

    /**
     * Returns the type of this service
     */
    fun getServiceType(): ServiceType

    /**
     * Returns the Unique Id of this service
     */
    fun getUniqueId(): UUID

    /**
     * Returns the name of this service.
     * e.g. Lobby-1
     */
    fun getName(): String

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
     * Returns the template that this service uses
     * e.g. Lobby
     */
    fun getTemplateName(): String

    /**
     * Returns the service group of this service
     */
    fun getServiceGroup(): ICloudServiceGroup

    /**
     * Returns the amount of players that are currently on this service
     */
    fun getOnlinePlayers(): Int

    /**
     * Returns the maximum amount of players for this service
     */
    fun getMaxPlayers(): Int

    /**
     * Returns the maximum amount of RAM for this service in MB
     */
    fun getMaxMemory(): Int

    /**
     * Returns the name of the wrapper this service is running on
     */
    fun getWrapperName(): String

    /**
     * Returns the host of this service
     */
    fun getHost(): String

    /**
     * Returns the port this service is bound to
     */
    fun getPort(): Int

    /**
     * Returns whether this service is logged in to the manager
     */
    fun isLoggedIn(): Boolean

    /**
     * Return whether the process of this service is alive
     */
    fun isProcessAlive(): Boolean

    /**
     * Returns whether this service is static.
     */
    fun isServiceStatic(): Boolean

    /**
     * Returns the MOTD of this service.
     */
    fun getMOTD(): String


}