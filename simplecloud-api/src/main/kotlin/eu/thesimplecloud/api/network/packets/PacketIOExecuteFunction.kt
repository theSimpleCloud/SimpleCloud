package eu.thesimplecloud.api.network.packets

import eu.thesimplecloud.api.exception.SerializationException
import eu.thesimplecloud.api.utils.NoArgsFunction
import eu.thesimplecloud.api.utils.ObjectSerializer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketIOExecuteFunction<T : Any>() : BytePacket() {


    constructor(function: () -> T) : this() {
        val noArgsFunction = object: NoArgsFunction<T> {
            override fun invoke(): T {
                return function()
            }
        }
        val serializeString = ObjectSerializer.serialize(noArgsFunction)
        this.buffer.writeBytes(serializeString.toByteArray(Charsets.ISO_8859_1))
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<out Any> {
        val string = String(this.buffer.array(), Charsets.ISO_8859_1)
        val noArgsFunction = ObjectSerializer.deserialize<NoArgsFunction<*>>(string) as NoArgsFunction<out Any>?
        noArgsFunction ?: return failure(SerializationException("Object was null"))
        return success(noArgsFunction.invoke())
    }
}