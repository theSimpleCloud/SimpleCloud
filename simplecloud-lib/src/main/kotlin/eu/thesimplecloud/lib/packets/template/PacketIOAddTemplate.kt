package eu.thesimplecloud.lib.packets.template

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.lib.CloudLib
import eu.thesimplecloud.lib.template.ITemplate
import eu.thesimplecloud.lib.template.impl.DefaultTemplate

class PacketIOAddTemplate() : ObjectPacket<ITemplate>(DefaultTemplate::class.java) {

    constructor(template: ITemplate) : this() {
        this.value = template
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        this.value?.let { CloudLib.instance.getTemplateManager().addTemplate(it) }
        return null
    }
}