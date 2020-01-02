package eu.thesimplecloud.lib.player.connection

class DefaultPlayerAddress(
        private val hostname: String,
        private val port: Int
) : IPlayerAddress {

    override fun getHostname(): String = this.hostname

    override fun getPort(): Int = this.port
}