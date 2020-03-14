package eu.thesimplecloud.module.permission.entity

import eu.thesimplecloud.module.permission.permission.Permission
import java.lang.IllegalStateException

open class PermissionEntity : IPermissionEntity {

    private val permissions = ArrayList<Permission>()

    override fun getAllPermissions(): Collection<Permission> = this.permissions

    override fun addPermission(permission: Permission) {
        if (getPermissionByName(permission.permissionString) != null) throw IllegalStateException("Permission already added")
        this.permissions.add(permission)
    }

    override fun removePermission(permissionString: String) {
        this.permissions.remove(getPermissionByName(permissionString))
    }

    override fun clearAllPermission() {
        this.permissions.clear()
    }
}