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

package eu.thesimplecloud.module.permission.service.bungee

import eu.thesimplecloud.module.permission.PermissionPool
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PermissionCheckEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeListener : Listener {

    @EventHandler
    fun on(event: PermissionCheckEvent) {
        val sender = event.sender
        if (sender is ProxiedPlayer) {
            val permissionPlayer = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(sender.uniqueId)
            if (permissionPlayer == null) {
                println("WARNING: PermissionPlayer is NULL (${sender.name})")
                return
            }
            event.setHasPermission(permissionPlayer.hasPermission(event.permission))
        }
    }

}