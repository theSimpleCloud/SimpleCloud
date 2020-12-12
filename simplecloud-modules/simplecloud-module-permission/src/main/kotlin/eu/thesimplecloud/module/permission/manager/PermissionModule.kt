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

package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.launcher.startup.Launcher
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.PermissionGroup
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager
import eu.thesimplecloud.module.permission.manager.command.PermissionCommand
import eu.thesimplecloud.module.permission.manager.config.PermissionGroupConfigsLoader
import eu.thesimplecloud.module.permission.manager.config.PermissionModuleConfigLoader
import eu.thesimplecloud.module.permission.packet.PacketInGetDefaultGroupName
import eu.thesimplecloud.module.permission.player.IPermissionPlayer
import eu.thesimplecloud.module.permission.player.PermissionPlayer

class PermissionModule : ICloudModule {
    companion object {

        @JvmStatic
        @Volatile
        lateinit var instance: PermissionModule
            private set

    }

    private lateinit var permissionGroupManager: PermissionGroupManager

    private val permissionGroupConfigsLoader =  PermissionGroupConfigsLoader()

    init {
        instance = this
    }

    override fun onEnable() {
        val config = PermissionModuleConfigLoader().loadConfig()
        val groups = this.permissionGroupConfigsLoader.loadAll()

        this.permissionGroupManager = PermissionGroupManager(groups.toList())
        this.permissionGroupManager.setDefaultPermissionGroup(config.defaultPermissionGroupName)
        PermissionPool(this.permissionGroupManager)
        CloudAPI.instance.getEventManager().registerListener(this, CloudListener())
        Launcher.instance.commandManager.registerCommand(this, PermissionCommand())
        Manager.instance.communicationServer.getPacketManager().registerPacket(PacketInGetDefaultGroupName::class.java)
    }

    override fun onDisable() {
    }

    fun updateGroupsFile() {
        val permissionGroups = this.permissionGroupManager.getAllPermissionGroups() as List<PermissionGroup>
        permissionGroups.forEach { this.permissionGroupConfigsLoader.save(it) }
    }

    fun updatePermissionPlayer(permissionPlayer: IPermissionPlayer) {
        val offlineCloudPlayerHandler = Manager.instance.offlineCloudPlayerHandler
        val cloudPlayer = permissionPlayer.getCloudPlayer().getNow()
        if (cloudPlayer != null) {
            permissionPlayer.update()
            offlineCloudPlayerHandler.saveCloudPlayer(cloudPlayer.toOfflinePlayer() as OfflineCloudPlayer)
        } else {
            val offlinePlayer = offlineCloudPlayerHandler.getOfflinePlayer(permissionPlayer.getName()) ?: return
            offlinePlayer.setProperty(PermissionPlayer.PROPERTY_NAME, permissionPlayer)
            offlineCloudPlayerHandler.saveCloudPlayer(offlinePlayer as OfflineCloudPlayer)
        }
    }
}