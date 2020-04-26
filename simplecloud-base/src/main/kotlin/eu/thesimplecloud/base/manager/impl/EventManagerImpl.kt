package eu.thesimplecloud.base.manager.impl

import eu.thesimplecloud.api.eventapi.BasicEventManager
import eu.thesimplecloud.api.eventapi.IEvent
import eu.thesimplecloud.api.eventapi.ISynchronizedEvent
import eu.thesimplecloud.api.extension.sendPacketToAllAuthenticatedNonWrapperClients
import eu.thesimplecloud.api.network.packets.event.PacketIOCallEvent
import eu.thesimplecloud.base.manager.startup.Manager

class EventManagerImpl : BasicEventManager() {

    override fun call(event: IEvent, fromPacket: Boolean) {
        super.call(event, fromPacket)
        if (event is ISynchronizedEvent)
            Manager.instance.communicationServer.getClientManager().sendPacketToAllAuthenticatedNonWrapperClients(PacketIOCallEvent(event))
    }

}