package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.event.player.CloudPlayerUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.module.permission.player.PermissionPlayer

class CloudListener : IListener {

    @CloudEventHandler
    fun on(event: CloudPlayerUpdatedEvent) {
        val cloudPlayer = event.cloudPlayer
        if (!cloudPlayer.hasProperty(PermissionPlayer.PROPERTY_NAME)) {
            cloudPlayer.setProperty(PermissionPlayer.PROPERTY_NAME, Property(PermissionPlayer(cloudPlayer.getName(), cloudPlayer.getUniqueId())))
        }

        val permissionPlayer = cloudPlayer.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)!!.getValue(this::class.java.classLoader)

        val expiredPermissions = permissionPlayer.getPermissions().filter { it.isExpired() }
        expiredPermissions.forEach { permissionPlayer.removePermission(it.permissionString) }
        val expiredGroups = permissionPlayer.getPermissionGroupInfoList().filter { it.isExpired() }
        expiredGroups.forEach { permissionPlayer.removePermissionGroup(it.permissionGroupName) }
        if (expiredPermissions.isNotEmpty() || expiredGroups.isNotEmpty()) {
            PermissionModule.instance.updatePermissionPlayer(permissionPlayer)
        }
    }

}