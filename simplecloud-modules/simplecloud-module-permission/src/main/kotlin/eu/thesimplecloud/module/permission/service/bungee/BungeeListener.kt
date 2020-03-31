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