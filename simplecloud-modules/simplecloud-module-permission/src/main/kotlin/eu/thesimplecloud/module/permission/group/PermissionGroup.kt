package eu.thesimplecloud.module.permission.group

import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.entity.PermissionEntity

class PermissionGroup(private val name: String) : PermissionEntity(), IPermissionGroup {

    private val inheritedPermissionGroups = ArrayList<String>()


    override fun getName(): String = this.name

    override fun addInheritedPermissionGroup(permissionGroup: IPermissionGroup): Boolean {
        if (this.inheritedPermissionGroups.contains(permissionGroup.getName())) return false
        this.inheritedPermissionGroups.add(permissionGroup.getName())
        return true
    }

    override fun removeInheritedPermissionGroup(permissionGroup: IPermissionGroup): Boolean {
        if (!this.inheritedPermissionGroups.contains(permissionGroup.getName())) return false
        this.inheritedPermissionGroups.remove(permissionGroup.getName())
        return true
    }

    override fun getAllInheritedPermissionGroupNames(): List<String> = this.inheritedPermissionGroups

    /**
     * Updates this group
     */
    fun update() {
        PermissionPool.instance.getPermissionGroupManager().update(this)
    }
}