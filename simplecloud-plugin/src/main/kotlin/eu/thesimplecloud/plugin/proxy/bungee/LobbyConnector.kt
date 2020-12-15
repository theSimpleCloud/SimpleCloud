/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.plugin.proxy.bungee

import eu.thesimplecloud.api.CloudAPI
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class LobbyConnector {

    fun getLobbyServer(player: ProxiedPlayer, filterServices: List<String> = emptyList()): ServerInfo? {
        val lobbyGroups = CloudAPI.instance.getCloudServiceGroupManager().getLobbyGroups()
        val sortedLobbyGroups = lobbyGroups.sortedByDescending { it.getPriority() }.filter { !it.isInMaintenance() || player.hasPermission("cloud.maintenance.join") }
        val groups = sortedLobbyGroups.filter { it.getPermission() == null || player.hasPermission(it.getPermission()) }
        val availableServices = groups.map { group -> group.getAllServices().filter { it.isOnline() }.filter { !it.isFull() } }.flatten()
        val serviceToConnectTo = availableServices.firstOrNull { !filterServices.contains(it.getName()) }
        if (serviceToConnectTo == null) println("WARNING: Lobby server was null.")
        return serviceToConnectTo?.let { ProxyServer.getInstance().getServerInfo(it.getName()) }
    }


}