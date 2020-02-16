package eu.thesimplecloud.api.extension

import eu.thesimplecloud.api.utils.IAuthenticatable
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

fun IClientManager<*>.getAllAuthenticatedClients(): List<IConnectedClient<out IConnectedClientValue>> {
    return this.getClients().filter { it.getClientValue() != null && (it.getClientValue() as IAuthenticatable).isAuthenticated() }
}

fun IClientManager<*>.sendPacketToAllAuthenticatedClients(packet: IPacket) {
    this.getAllAuthenticatedClients().forEach { it.sendUnitQuery(packet) }
}

fun IClientManager<*>.sendPacketToAllAuthenticatedNonWrapperClients(packet: IPacket) {
    this.getAllAuthenticatedClients().filter { it.getClientValue() !is IWrapperInfo }.forEach { it.sendUnitQuery(packet) }
}