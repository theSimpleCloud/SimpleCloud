package eu.thesimplecloud.module.permission.player.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.module.permission.player.IPermissionPlayer
import eu.thesimplecloud.module.permission.player.PermissionPlayer
import java.util.*

interface IPermissionPlayerManager {


    /**
     * Returns a list of all cached permission players
     */
    fun getAllCachedPermissionPlayers(): List<IPermissionPlayer> = CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects().mapNotNull { it.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue(this::class.java.classLoader) }

    /**
     * Returns the first [IPermissionPlayer] found by the specified [name]
     */
    fun getCachedPermissionPlayer(name: String): IPermissionPlayer? = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(name)?.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue(this::class.java.classLoader)

    /**
     * Returns the first [IPermissionPlayer] found by the specified [uniqueId]
     */
    fun getCachedPermissionPlayer(uniqueId: UUID): IPermissionPlayer? = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)?.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue(this::class.java.classLoader)

    /**
     * Sends a packet to the manager to get the requested [IPermissionPlayer] and returns its result
     */
    fun getPermissionPlayer(name: String): ICommunicationPromise<IPermissionPlayer> = CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(name).then { it.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue(this::class.java.classLoader) }

    /**
     * Sends a packet to the manager to get the requested [IPermissionPlayer] and returns its result
     */
    fun getPermissionPlayer(uniqueId: UUID): ICommunicationPromise<IPermissionPlayer> = CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(uniqueId).then { it.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue(this::class.java.classLoader) }


}