package eu.thesimplecloud.module.permission.service.velocity

import com.velocitypowered.api.permission.PermissionFunction
import com.velocitypowered.api.permission.PermissionProvider
import com.velocitypowered.api.permission.PermissionSubject
import com.velocitypowered.api.permission.Tristate
import com.velocitypowered.api.proxy.Player
import eu.thesimplecloud.module.permission.PermissionPool

class VelocityPermissionProvider : PermissionProvider {

    override fun createFunction(subject: PermissionSubject): PermissionFunction? {
        if (subject !is Player) return null
        return CloudPermissionFunction(subject)
    }

    private class CloudPermissionFunction(private val player: Player) : PermissionFunction {


        override fun getPermissionValue(permission: String?): Tristate {
            if (permission == null) return Tristate.FALSE
            val permissionPlayer = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(player.uniqueId)
            if (permissionPlayer == null) {
                println("WARNING: PermissionPlayer is NULL (${player.username})")
                return Tristate.FALSE
            }
            return Tristate.fromBoolean(permissionPlayer.hasPermission(permission))
        }

    }

}