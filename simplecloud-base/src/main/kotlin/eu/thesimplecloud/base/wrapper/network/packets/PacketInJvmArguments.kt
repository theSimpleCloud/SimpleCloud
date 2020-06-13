package eu.thesimplecloud.base.wrapper.network.packets

import eu.thesimplecloud.base.core.jvm.JvmArgumentsConfig
import eu.thesimplecloud.base.wrapper.startup.Wrapper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 12.06.2020
 * Time: 18:57
 */
class PacketInJvmArguments() : ObjectPacket<JvmArgumentsConfig>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val value = this.value?: return contentException("value")
        Wrapper.instance.jvmArgumentsConfig = value

        return unit()
    }

}