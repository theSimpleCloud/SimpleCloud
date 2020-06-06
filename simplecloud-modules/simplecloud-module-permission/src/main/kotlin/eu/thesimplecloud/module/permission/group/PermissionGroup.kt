/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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