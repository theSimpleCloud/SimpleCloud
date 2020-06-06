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

package eu.thesimplecloud.plugin.extension

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.exception.NoSuchPlayerException
import eu.thesimplecloud.api.player.ICloudPlayer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Returns the CloudPlayer found by the players uuid
 */
fun ProxiedPlayer.getCloudPlayer(): ICloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(this.uniqueId) ?: throw NoSuchPlayerException("Cannot find CloudPlayer by uuid: $uniqueId")

/**
 * Returns the CloudPlayer found by the players uuid
 */
fun Player.getCloudPlayer(): ICloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(this.uniqueId) ?: throw NoSuchPlayerException("Cannot find CloudPlayer by uuid: $uniqueId")

/**
 * Returns the CloudPlayer found by the players uuid
 */
fun com.velocitypowered.api.proxy.Player.getCloudPlayer(): ICloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(this.uniqueId) ?: throw NoSuchPlayerException("Cannot find CloudPlayer by uuid: $uniqueId")

/**
 * Returns the PrxiedPlayer found by the uuid of the [ICloudPlayer]
 */
fun ICloudPlayer.getProxiedPlayer(): ProxiedPlayer = ProxyServer.getInstance().getPlayer(this.getUniqueId()) ?: throw NoSuchPlayerException("Cannot find ProxiedPlayer by uuid: ${getUniqueId()}")

/**
 * Returns the bukkit player found by the uuid of the [ICloudPlayer]
 */
fun ICloudPlayer.getBukkitPlayer(): Player = Bukkit.getPlayer(this.getUniqueId()) ?: throw NoSuchPlayerException("Cannot find bukkit player by uuid: ${getUniqueId()}")