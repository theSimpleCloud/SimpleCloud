package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.connection.IPlayerConnection
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
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

    private var online = true

    override fun getPlayerConnection(): IPlayerConnection = this.playerConnection

    override fun getConnectedProxyName(): String = this.connectedProxyName

    override fun getConnectedServerName(): String? = this.connectedServerName

    override fun toOfflinePlayer(): IOfflineCloudPlayer = OfflineCloudPlayer(getName(), getUniqueId(), getFirstLogin(), getLastLogin(), getOnlineTime(), this.properties)

    override fun isOnline(): Boolean = this.online

    fun setOffline() {
        this.online = false
    }

    fun setConnectedProxyName(name: String) {
        this.connectedProxyName = name
    }

    fun setConnectedServerName(name: String?) {
        this.connectedServerName = name
    }


    override fun getOnlineTime(): Long {
        return (System.currentTimeMillis() - getLastLogin()) + super.getOnlineTime()
    }

    override fun toString(): String {
        return JsonData.fromObjectWithGsonExclude(this).getAsJsonString()
    }

}