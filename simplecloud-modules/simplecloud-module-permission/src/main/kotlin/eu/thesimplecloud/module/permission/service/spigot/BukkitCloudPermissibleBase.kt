package eu.thesimplecloud.module.permission.service.spigot

import eu.thesimplecloud.module.permission.PermissionPool
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission

class BukkitCloudPermissibleBase(private val player: Player) : PermissibleBase(player) {

    private val permissionPlayer = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(player.uniqueId)

    override fun isPermissionSet(name: String): Boolean {
        return permissionPlayer?.hasPermission(name) ?: false
    }

    override fun isPermissionSet(perm: Permission): Boolean {
        return permissionPlayer?.hasPermission(perm.name) ?: false
    }

    override fun hasPermission(inName: String): Boolean {
        if (inName.equals("bukkit.broadcast.user", ignoreCase = true)) {
            return true
        }
        if (permissionPlayer == null)
            println("WARNING: PermissionPlayer of " + player.uniqueId + " is null.")

        return permissionPlayer?.hasPermission(inName) ?: false
    }

    override fun hasPermission(perm: Permission): Boolean {
        return permissionPlayer?.hasPermission(perm.name) ?: false
    }

    override fun isOp(): Boolean {
        return permissionPlayer?.hasAllRights() ?: false
    }

}