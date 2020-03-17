package eu.thesimplecloud.module.permission.group.manager

import eu.thesimplecloud.api.sync.list.AbstractSynchronizedObjectList
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.module.permission.group.IPermissionGroup
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.manager.PermissionModule

class PermissionGroupManager() : AbstractSynchronizedObjectList<PermissionGroup>(), IPermissionGroupManager {

    private var defaultPermissionGroupName = "default"

    override fun getIdentificationName(): String = "simplecloud-module-permission"

    override fun getCachedObjectByUpdateValue(value: PermissionGroup): PermissionGroup? {
        return getPermissionGroupByName(value.getName())
    }

    override fun getAllPermissionGroups(): Collection<IPermissionGroup> = this.values

    override fun getPermissionGroupByName(name: String): PermissionGroup? = this.values.firstOrNull { it.getName() == name }

    override fun getDefaultPermissionGroupName(): String = this.defaultPermissionGroupName

    override fun update(permissionGroup: PermissionGroup) {
        super.update(permissionGroup, false)

        JsonData.fromObject(this).saveAsFile(PermissionModule.GROUPS_FILE)
    }

    fun setDefaultPermissionGroup(groupName: String) {
        this.defaultPermissionGroupName = groupName
    }

}