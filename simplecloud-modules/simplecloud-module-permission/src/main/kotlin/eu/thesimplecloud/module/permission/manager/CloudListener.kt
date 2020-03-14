package eu.thesimplecloud.module.permission.manager

import eu.thesimplecloud.api.event.player.CloudPlayerUpdatedEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.player.PermissionPlayer

class CloudListener : IListener {

    @CloudEventHandler
    fun on(event: CloudPlayerUpdatedEvent) {
        val cloudPlayer = event.cloudPlayer
        if (!cloudPlayer.hasProperty(PermissionPlayer.PROPERTY_NAME)) {
            cloudPlayer.setProperty(PermissionPlayer.PROPERTY_NAME, Property(PermissionPlayer(cloudPlayer.getName(), cloudPlayer.getUniqueId(), PermissionPool.instance.getPermissionGroupManager().getDefaultPermissionGroupName())))
        }
    }

}