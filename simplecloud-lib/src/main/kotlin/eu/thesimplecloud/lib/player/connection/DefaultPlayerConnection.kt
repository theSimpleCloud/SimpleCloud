package eu.thesimplecloud.lib.player.connection

import java.net.InetSocketAddress
import java.util.*

data class DefaultPlayerConnection(
        private val address: DefaultPlayerAddress,
        private val name: String,
        private val uniqueId: UUID,
        private val onlineMode: Boolean,
        private val version: Int
) : IPlayerConnection {

    override fun getAddress(): IPlayerAddress = this.address

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getName(): String = this.name

    override fun isOnlineMode(): Boolean = this.onlineMode

    override fun getVersion(): Int = this.version
}