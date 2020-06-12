package eu.thesimplecloud.base.manager.network.packets

import eu.thesimplecloud.base.core.jvm.JvmArgumentsConfig
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.omg.CORBA.Object

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 12.06.2020
 * Time: 18:55
 */
class PacketOutJvmArguments() : ObjectPacket<JvmArgumentsConfig>() {

    constructor(jvmArgumentsConfig: JvmArgumentsConfig): this() {
        value = jvmArgumentsConfig
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        return unit()
    }
}