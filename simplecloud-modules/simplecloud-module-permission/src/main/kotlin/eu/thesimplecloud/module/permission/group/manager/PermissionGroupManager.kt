package eu.thesimplecloud.module.permission.group.manager

import eu.thesimplecloud.api.sync.list.AbstractSynchronizedObjectList
import eu.thesimplecloud.module.permission.group.IPermissionGroup
import eu.thesimplecloud.module.permission.group.PermissionGroup

class PermissionGroupManager() : AbstractSynchronizedObjectList<PermissionGroup>(), IPermissionGroupManager {

    private var defaultPermissionGroupName = "default"

    override fun getIdentificationName(): String = "simplecloud-module-permission"

    override fun getCachedObjectByUpdateValue(value: PermissionGroup): PermissionGroup? {
        return getPermissionGroupByName(value.getName())
    }

    override fun getAllPermissionGroups(): Collection<IPermissionGroup> = this.values

    override fun getPermissionGroupByName(name: String): PermissionGroup? = this.values.firstOrNull { it.getName() == name }

    override fun getDefaultPermissionGroupName(): String = this.defaultPermissionGroupName

    fun setDefaultPermissionGroup(groupName: String) {
        this.defaultPermissionGroupName = groupName
    }

}