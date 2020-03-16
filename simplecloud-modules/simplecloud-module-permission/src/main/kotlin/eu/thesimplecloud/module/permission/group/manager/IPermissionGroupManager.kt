package eu.thesimplecloud.module.permission.group.manager

import eu.thesimplecloud.module.permission.group.IPermissionGroup

interface IPermissionGroupManager {

    /**
     * Returns all registered permission groups.
     */
    fun getAllPermissionGroups(): Collection<IPermissionGroup>

    /**
     * Returns the [IPermissionGroup] found by the specified [name]
     */
    fun getPermissionGroupByName(name: String): IPermissionGroup?

    /**
     * Returns the name of the default permission group
     */
    fun getDefaultPermissionGroupName(): String

    /**
     * Returns the default [IPermissionGroup]
     */
    fun getDefaultPermissionGroup(): IPermissionGroup = getPermissionGroupByName(getDefaultPermissionGroupName())!!

}