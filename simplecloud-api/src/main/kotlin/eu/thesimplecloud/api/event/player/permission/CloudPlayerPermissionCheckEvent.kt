package eu.thesimplecloud.api.event.player.permission

import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.player.ICloudPlayer

class CloudPlayerPermissionCheckEvent(
        val cloudPlayer: ICloudPlayer,
        val permission: String,
        var state: PermissionState = PermissionState.UNKNOWN
) : IEvent {


    fun setHasPermission(boolean: Boolean) {
        this.state = PermissionState.valueOf(boolean.toString().toUpperCase())
    }

}