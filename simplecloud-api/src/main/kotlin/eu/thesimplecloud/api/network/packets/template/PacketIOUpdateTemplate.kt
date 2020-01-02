package eu.thesimplecloud.api.network.packets.template

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.template.ITemplate

class PacketIOUpdateTemplate() : ObjectPacket<ITemplate>() {

    constructor(template: ITemplate) : this() {
        this.value = template
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val value = this.value ?: return contentException("value")
        CloudAPI.instance.getTemplateManager().updateTemplate(value)
        return unit()
    }
}