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