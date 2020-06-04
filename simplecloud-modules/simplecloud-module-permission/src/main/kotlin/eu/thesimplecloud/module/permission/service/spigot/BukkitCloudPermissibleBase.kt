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

package eu.thesimplecloud.module.permission.service.spigot

import eu.thesimplecloud.module.permission.PermissionPool
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission

class BukkitCloudPermissibleBase(private val player: Player) : PermissibleBase(player) {


    fun getPermissionPlayer() = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(player.uniqueId)

    override fun isPermissionSet(name: String): Boolean {
        return getPermissionPlayer()?.hasPermission(name) ?: false
    }

    override fun isPermissionSet(perm: Permission): Boolean {
        return getPermissionPlayer()?.hasPermission(perm.name) ?: false
    }

    override fun hasPermission(inName: String): Boolean {
        if (inName.equals("bukkit.broadcast.user", ignoreCase = true)) {
            return true
        }
        if (getPermissionPlayer() == null)
            println("WARNING: PermissionPlayer of " + player.uniqueId + " is null.")

        return getPermissionPlayer()?.hasPermission(inName) ?: false
    }

    override fun hasPermission(perm: Permission): Boolean {
        return getPermissionPlayer()?.hasPermission(perm.name) ?: false
    }

    override fun isOp(): Boolean {
        return getPermissionPlayer()?.hasAllRights() ?: false
    }

    override fun recalculatePermissions() {

    }

}