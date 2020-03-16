package eu.thesimplecloud.module.permission.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.module.permission.permission.Permission

interface IPermissionEntity {

    /**
     * Returns whether the group has the specified [permission]
     */
    fun hasPermission(permission: String): Boolean {
        val permissionObj = getAllNotExpiredPermissions().firstOrNull { it.permissionString == permission } ?: return hasAllRights()
        return permissionObj.active
    }

    /**
     * Returns the permission object of the specified [permission]
     */
    fun getPermissionByName(permission: String): Permission? = getAllPermissions().firstOrNull { it.permissionString == permission }

    /**
     * Returns all permissions of this entity
     */
    fun getAllPermissions(): Collection<Permission>

    /**
     * Adds the specified [permission] to the list
     */
    fun addPermission(permission: Permission)

    /**
     * Removes the permission object found by the specified [permissionString]
     */
    fun removePermission(permissionString: String)

    /**
     * Removes all permissions from this entity
     */
    fun clearAllPermission()

    /**
    * Returns all not expired permissions
    */
    @JsonIgnore
    fun getAllNotExpiredPermissions(): Collection<Permission> = getAllPermissions().filter { !it.isExpired() }

    /**
     * Returns whether this
     */
    fun hasAllRights(): Boolean = getAllNotExpiredPermissions().any { it.permissionString == "*" }

}