package eu.thesimplecloud.lib.packets.template

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.impl.DefaultTemplate

class PacketIODeleteTemplate() : ObjectPacket<String>(String::class.java) {

    constructor(name: String) : this() {
        this.value = name
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        this.value?.let { CloudLib.instance.getTemplateManager().removeTemplate(it) }
        return null
    }

}