package eu.thesimplecloud.module.permission.entity

import com.fasterxml.jackson.annotation.JsonInclude
import eu.thesimplecloud.module.permission.permission.Permission

open class PermissionEntity : IPermissionEntity {

    private val permissions = ArrayList<Permission>()

    @JsonInclude
    override fun getPermissions(): Collection<Permission> = this.permissions

    override fun addPermission(permission: Permission) {
        removePermission(permission.permissionString)
        //if (getPermissionByName(permission.permissionString) != null) throw IllegalStateException("Permission already added")
        this.permissions.add(permission)
    }

    override fun removePermission(permissionString: String) {
        this.permissions.remove(getPermissionByName(permissionString))
    }

    override fun clearAllPermission() {
        this.permissions.clear()
    }
}