package eu.thesimplecloud.base.manager.packet

import com.google.common.collect.Maps
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

class PacketRegistry : IPacketRegistry {

    private val registeredPackets = Maps.newConcurrentMap<ICloudModule, MutableList<Class<out IPacket>>>()

    override fun registerPacket(cloudModule: ICloudModule, packetClass: Class<out IPacket>) {
        val list = this.registeredPackets.getOrPut(cloudModule) { ArrayList() }
        list.add(packetClass)
        val packetManager = Manager.instance.communicationServer.getPacketManager()
        packetManager.registerPacket(packetClass)
    }

    override fun unregisterAllPackets(cloudModule: ICloudModule) {
        val list = this.registeredPackets[cloudModule] ?: emptyList<Class<out IPacket>>()
        list.forEach { Manager.instance.communicationServer.getPacketManager().unregisterPacket(it) }
        this.registeredPackets.remove(cloudModule)
    }
}