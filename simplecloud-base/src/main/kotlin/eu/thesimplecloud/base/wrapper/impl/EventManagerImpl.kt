package eu.thesimplecloud.base.wrapper.impl

import eu.thesimplecloud.api.eventapi.BasicEventManager
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.eventapi.ISynchronizedEvent
import eu.thesimplecloud.api.network.packets.event.PacketIOCallEvent
import eu.thesimplecloud.base.wrapper.startup.Wrapper

class EventManagerImpl : BasicEventManager() {

    override fun call(event: IEvent, fromPacket: Boolean) {
        //don't call event if fromPacket is true because the event will be called via the received packet.
        if (!fromPacket && event is ISynchronizedEvent) {
            Wrapper.instance.communicationClient.sendUnitQuery(PacketIOCallEvent(event))
        } else {
            super.call(event, fromPacket)
        }
    }

}