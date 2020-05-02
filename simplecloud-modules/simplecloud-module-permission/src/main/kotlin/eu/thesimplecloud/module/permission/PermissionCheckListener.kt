package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.event.player.permission.CloudPlayerPermissionCheckEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.module.permission.player.getPermissionPlayer

class PermissionCheckListener : IListener {

    @CloudEventHandler
    fun on (event: CloudPlayerPermissionCheckEvent) {
        event.setHasPermission(event.cloudPlayer.getPermissionPlayer(this::class.java.classLoader).hasPermission(event.permission))
    }

}