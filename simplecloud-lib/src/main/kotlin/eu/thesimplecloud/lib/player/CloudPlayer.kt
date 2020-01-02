package eu.thesimplecloud.lib.player

import eu.thesimplecloud.lib.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.lib.player.connection.IPlayerConnection
import java.util.*

class CloudPlayer(
        name: String,
        uniqueId: UUID,
        firstLogin: Long,
        lastLogin: Long,
        onlineTime: Long,
        private var connectedProxyName: String,
        private var connectedServerName: String?,
        private val playerConnection: DefaultPlayerConnection
) : OfflineCloudPlayer(
        name,
        uniqueId,
        firstLogin,
        lastLogin,
        onlineTime
), ICloudPlayer {

    override fun getPlayerConnection(): IPlayerConnection = this.playerConnection

    override fun getConnectedProxyName(): String = this.connectedProxyName

    override fun getConnectedServerName(): String? = this.connectedServerName

    override fun toOfflinePlayer(): IOfflineCloudPlayer = OfflineCloudPlayer(getName(), getUniqueId(), getFirstLogin(), getLastLogin(), getOnlineTime())

    fun setConnectedProxyName(name: String) {
        this.connectedProxyName = name
    }

    fun setConnectedServerName(name: String?) {
        this.connectedServerName = name
    }

}