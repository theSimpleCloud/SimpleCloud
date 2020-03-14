package eu.thesimplecloud.module.permission.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.property.Property
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
     * Returns the permission groups name of this player
     */
    fun getPermissionGroupName(): String

    /**
     * Returns the [IPermissionGroup] of this player
     */
    @JsonIgnore
    fun getPermissionGroup(): IPermissionGroup = PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(getPermissionGroupName()) ?: PermissionPool.instance.getPermissionGroupManager().getDefaultPermissionGroup()

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

    override fun hasPermission(permission: String): Boolean {
        return super.hasPermission(permission) || getPermissionGroup().hasPermission(permission)
    }

}