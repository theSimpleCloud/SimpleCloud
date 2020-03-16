package eu.thesimplecloud.module.permission.group

import eu.thesimplecloud.api.sync.list.ISynchronizedListObject
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.entity.IPermissionEntity

interface IPermissionGroup : IPermissionEntity, ISynchronizedListObject {

    /**
     * Returns the name of this group.
     */
    fun getName(): String

    /**
     * Adds a permission group to inherit from.
     * Returns false if this group is already inheriting from the specified one.
     */
    fun addInheritedPermissionGroup(permissionGroup: IPermissionGroup): Boolean

    /**
     * Removes a permission group from the inheritance.
     * Returns whether this groups was inheriting from the specified [permissionGroup]
     */
    fun removeInheritedPermissionGroup(permissionGroup: IPermissionGroup): Boolean

    /**
     * Returns the names of all permission groups this group inherits from.
     */
    fun getAllInheritedPermissionGroupNames(): List<String>

    /**
     * Returns all permission groups this group inherits from.
     */
    fun getAllInheritedPermissionGroups(): List<IPermissionGroup> = getAllInheritedPermissionGroupNames().mapNotNull { PermissionPool.instance.getPermissionGroupManager().getPermissionGroupByName(it) }


    override fun hasPermission(permission: String): Boolean {
        return super.hasPermission(permission) || getAllInheritedPermissionGroups().any { it.hasPermission(permission) }
    }

}