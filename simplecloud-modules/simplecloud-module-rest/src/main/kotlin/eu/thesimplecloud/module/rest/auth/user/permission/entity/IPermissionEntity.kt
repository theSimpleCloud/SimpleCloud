/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.module.rest.auth.user.permission.entity

import eu.thesimplecloud.module.rest.auth.user.permission.Permission


interface IPermissionEntity {

    /**
     * Returns whether the group has the specified [permission]
     */
    fun hasPermission(permission: String): Boolean {
        if (permission.isBlank()) return true
        val permissionObj = getPermissionByMatch(permission) ?: return hasAllRights()
        return permissionObj.active
    }

    fun getPermissionByMatch(permission: String): Permission? {
        val notExpiredPermissions = getPermissions()
        return notExpiredPermissions.firstOrNull { it.matches(permission) }
    }

    /**
     * Returns the permission object of the specified [permission]
     */
    fun getPermissionByName(permission: String): Permission? =
        getPermissions().firstOrNull { it.permissionString == permission }

    /**
     * Returns all permissions of this entity
     */
    fun getPermissions(): Collection<Permission>

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
     * Returns whether this
     */
    fun hasAllRights(): Boolean = getPermissions().any { it.permissionString == "*" }

}