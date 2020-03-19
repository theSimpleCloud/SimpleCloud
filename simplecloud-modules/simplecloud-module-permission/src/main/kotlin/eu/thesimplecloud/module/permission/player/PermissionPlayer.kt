package eu.thesimplecloud.module.permission.player

import com.fasterxml.jackson.annotation.JsonInclude
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.entity.PermissionEntity
import java.util.*
import kotlin.collections.ArrayList

class PermissionPlayer(
        private val name: String,
        private val uniqueId: UUID,
        @JsonInclude
        private val permissionGroupInfoList: MutableList<PlayerPermissionGroupInfo> = ArrayList()
) : PermissionEntity(), IPermissionPlayer {

    override fun getName(): String = this.name

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getPermissionGroupInfoList(): Collection<PlayerPermissionGroupInfo> {
        if (!permissionGroupInfoList.map { it.permissionGroupName }.contains(PermissionPool.instance.getPermissionGroupManager().getDefaultPermissionGroupName()))
            permissionGroupInfoList.add(PlayerPermissionGroupInfo(PermissionPool.instance.getPermissionGroupManager().getDefaultPermissionGroupName(), -1))
        return permissionGroupInfoList
    }

    override fun update(): ICommunicationPromise<Unit> {
        return getCloudPlayer().then {
            it.setProperty(PROPERTY_NAME, Property(this))
            it.update()
        }
    }

    override fun addPermissionGroup(group: PlayerPermissionGroupInfo) {
        removePermissionGroup(group.permissionGroupName)
        this.permissionGroupInfoList.add(group)
    }

    override fun removePermissionGroup(name: String) {
        this.permissionGroupInfoList.removeIf { it.permissionGroupName == name }
    }

    companion object {
        const val PROPERTY_NAME = "simplecloud-module-permission-player"
    }

}