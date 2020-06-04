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

package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.permission.group.manager.IPermissionGroupManager
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.manager.PermissionModule
import eu.thesimplecloud.module.permission.player.manager.IPermissionPlayerManager
import eu.thesimplecloud.plugin.startup.CloudPlugin

class PermissionPool(private val permissionGroupManager: PermissionGroupManager) : IPermissionPool {

    private val permissionPlayerManager = object : IPermissionPlayerManager {}

    init {
        instance = this
        CloudAPI.instance.getSynchronizedObjectListManager().registerSynchronizedObjectList(permissionGroupManager)
        val cloudModule = if (CloudAPI.instance.isManager()) {
            PermissionModule.instance
        } else {
            CloudPlugin.instance
        }
        CloudAPI.instance.getEventManager().registerListener(cloudModule, PermissionCheckListener())
    }


    override fun getPermissionGroupManager(): IPermissionGroupManager = this.permissionGroupManager

    override fun getPermissionPlayerManager(): IPermissionPlayerManager = this.permissionPlayerManager

    companion object {
        @JvmStatic
        lateinit var instance: PermissionPool
            private set
    }

}