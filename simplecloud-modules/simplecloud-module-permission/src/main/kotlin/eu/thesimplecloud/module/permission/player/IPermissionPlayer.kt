package eu.thesimplecloud.module.permission.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.utils.Nameable
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.entity.IPermissionEntity
import eu.thesimplecloud.module.permission.group.IPermissionGroup
import java.util.*

interface IPermissionPlayer : IPermissionEntity, Nameable {

    /**
     * Returns the uuid of this player
     */
    fun getUniqueId(): UUID

    /**
     * Returns the permission group info list
     */
    fun getPermissionGroupInfoList(): Collection<PlayerPermissionGroupInfo>

    /**
     * Returns the permission group info list
     */
    @JsonIgnore
    fun getAllNotExpiredPermissionGroupInfoList(): Collection<PlayerPermissionGroupInfo> = getPermissionGroupInfoList().filter { !it.isExpired() }

    /**
     * Returns whether this player has the specified permission group.
     * (case insensitive)
     */
    @JsonIgnore
    fun hasPermissionGroup(name: String): Boolean = getPermissionGroupInfoList().map { it.permissionGroupName.toLowerCase() }.contains(name)

    /**
     * Returns the [IPermissionGroup] of this player
     */
    @JsonIgnore
    fun getAllNotExpiredPermissionGroups(): List<IPermissionGroup> = getAllNotExpiredPermissionGroupInfoList().mapNotNull { PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(it.permissionGroupName) }

    /**
     * Returns the the [ICloudPlayer] of this permission player wrapped in a promise
     */
    @JsonIgnore
    fun getCloudPlayer(): ICommunicationPromise<ICloudPlayer> = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(getUniqueId())

    /**
     * Returns a promise that is completed when the operation is done. [ICommunicationPromise.isSuccess] indicates success or failure.
     */
    @JsonIgnore
    fun update(): ICommunicationPromise<Unit>

    /**
     * Adds a permission group to this player
     */
    fun addPermissionGroup(group: PlayerPermissionGroupInfo)

    /**
     * Removes a permission group from this player
     */
    fun removePermissionGroup(name: String)

    override fun hasPermission(permission: String): Boolean {
        return super.hasPermission(permission) || getAllNotExpiredPermissionGroups().any { it.hasPermission(permission) }
    }

}