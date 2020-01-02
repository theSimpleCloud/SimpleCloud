package eu.thesimplecloud.base.manager.events

import eu.thesimplecloud.lib.eventapi.ICancellable
import eu.thesimplecloud.lib.eventapi.IEvent
import eu.thesimplecloud.lib.player.ICloudPlayer

class CloudPlayerLoginEvent(
        val cloudPlayer: ICloudPlayer
) : IEvent, ICancellable {

    private var cancelled: Boolean = false
    var kickMessage: String = "§cLogin cancelled"


    override fun isCancelled(): Boolean = this.cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

}