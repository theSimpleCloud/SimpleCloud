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