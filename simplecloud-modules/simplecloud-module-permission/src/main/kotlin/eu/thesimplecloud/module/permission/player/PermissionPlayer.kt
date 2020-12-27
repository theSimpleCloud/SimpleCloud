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

package eu.thesimplecloud.module.permission.player

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.entity.PermissionEntity
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class PermissionPlayer(
        private val name: String,
        private val uniqueId: UUID,
        private val permissionGroupInfoList: CopyOnWriteArrayList<PlayerPermissionGroupInfo> = CopyOnWriteArrayList()
) : PermissionEntity(), IPermissionPlayer {

    override fun getName(): String = this.name

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getPermissionGroupInfoList(): Collection<PlayerPermissionGroupInfo> {
        if (!permissionGroupInfoList.map { it.permissionGroupName }.contains(PermissionPool.instance.getPermissionGroupManager().getDefaultPermissionGroupName()))
            permissionGroupInfoList.add(PlayerPermissionGroupInfo(PermissionPool.instance.getPermissionGroupManager().getDefaultPermissionGroupName(), -1))
        return permissionGroupInfoList
    }

    override fun update(): ICommunicationPromise<Unit> {
        return getOfflineCloudPlayer().then {
            it.setProperty(PROPERTY_NAME, this)
            it.update()
        }.flatten()
    }

    override fun addPermissionGroup(group: PlayerPermissionGroupInfo) {
        removePermissionGroup(group.permissionGroupName)
        this.permissionGroupInfoList.add(group)
    }

    override fun removePermissionGroup(name: String) {
        this.permissionGroupInfoList.removeIf { it.permissionGroupName == name }
    }

    override fun clearGroups() {
        this.permissionGroupInfoList.clear()
    }

    companion object {
        const val PROPERTY_NAME = "simplecloud-module-permission-player"
    }

}