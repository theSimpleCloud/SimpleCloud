/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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