package eu.thesimplecloud.module.permission

import eu.thesimplecloud.api.event.player.permission.CloudPlayerPermissionCheckEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.module.permission.player.PermissionPlayer

class PermissionCheckListener : IListener {

    @CloudEventHandler
    fun on (event: CloudPlayerPermissionCheckEvent) {
        val cloudPlayer = event.cloudPlayer
        val permissionPlayer = cloudPlayer.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)!!.getValue(this::class.java.classLoader)
        event.setHasPermission(permissionPlayer.hasPermission(event.permission))
    }

}