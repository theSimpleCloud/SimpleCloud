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

package eu.thesimplecloud.module.permission.group.manager

import eu.thesimplecloud.api.sync.`object`.SynchronizedObjectHolder
import eu.thesimplecloud.api.sync.list.AbstractSynchronizedObjectList
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.module.permission.group.IPermissionGroup
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.manager.PermissionModule

class PermissionGroupManager() : AbstractSynchronizedObjectList<PermissionGroup>(), IPermissionGroupManager {

    private var defaultPermissionGroupName = "default"

    override fun getIdentificationName(): String = "simplecloud-module-permission"

    override fun getCachedObjectByUpdateValue(value: PermissionGroup): SynchronizedObjectHolder<PermissionGroup>? {
        return this.values.firstOrNull { it.obj.getName() == value.getName() }
    }

    override fun getAllPermissionGroups(): Collection<IPermissionGroup> = this.values.map { it.obj }

    override fun getPermissionGroupByName(name: String): IPermissionGroup? = this.values.firstOrNull { it.obj.getName() == name }?.obj

    override fun getDefaultPermissionGroupName(): String = this.defaultPermissionGroupName

    override fun update(permissionGroup: PermissionGroup) {
        super.update(permissionGroup, false)

        JsonLib.fromObject(this).saveAsFile(PermissionModule.GROUPS_FILE)
    }

    fun setDefaultPermissionGroup(groupName: String) {
        this.defaultPermissionGroupName = groupName
    }

}