package eu.thesimplecloud.lib.player

import java.util.*

class CloudPlayer(
        name: String,
        uniqueId: UUID,
        private val version: Int,
        lastLogin: Long,
        onlineTime: Long,
        private var connectedProxyName: String,
        private var connectedServerName: String?
) : OfflineCloudPlayer(
        name,
        uniqueId,
        lastLogin,
        onlineTime
), ICloudPlayer {

    override fun getVersion(): Int = this.version

    override fun getConnectedProxyName(): String = this.connectedProxyName

    override fun getConnectedServerName(): String? = this.connectedServerName

    fun setConnectedProxyName(name: String) {
        this.connectedProxyName = name
    }

    fun setConnectedServerName(name: String?) {
        this.connectedServerName = name
    }

}