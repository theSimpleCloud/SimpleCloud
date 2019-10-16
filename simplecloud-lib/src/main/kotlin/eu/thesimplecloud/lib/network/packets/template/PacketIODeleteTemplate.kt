package eu.thesimplecloud.lib.network.packets.template

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib

class PacketIODeleteTemplate() : ObjectPacket<String>(String::class.java) {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        this.value?.let { CloudLib.instance.getTemplateManager().removeTemplate(it) }
        return null
    }

}