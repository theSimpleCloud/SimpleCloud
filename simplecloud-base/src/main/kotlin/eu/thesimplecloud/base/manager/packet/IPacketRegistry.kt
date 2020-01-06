package eu.thesimplecloud.base.manager.packet

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import org.reflections.Reflections

interface IPacketRegistry {

    /**
     * Registers a packet
     */
    fun registerPacket(cloudModule: ICloudModule, packetClass: Class<out IPacket>)

    /**
     * Registers all Packets in the specified [packages]
     */
    fun registerPacketPackage(cloudModule: ICloudModule, vararg packages: String) {
        for (packageName in packages) {
            val reflections = Reflections(packageName)
            val allClasses = reflections.getSubTypesOf(IPacket::class.java)
                    .union(reflections.getSubTypesOf(JsonPacket::class.java))
                    .union(reflections.getSubTypesOf(ObjectPacket::class.java))
                    .union(reflections.getSubTypesOf(BytePacket::class.java))
                    .filter { it != JsonPacket::class.java && it != BytePacket::class.java && it != ObjectPacket::class.java }

            allClasses.forEach { packetClass -> registerPacket(cloudModule, packetClass) }
        }
    }

    /**
     * Unregisters a packet
     */
    fun unregisterAllPackets(cloudModule: ICloudModule)

}