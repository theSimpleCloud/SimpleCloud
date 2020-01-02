package eu.thesimplecloud.api.player

import java.util.*
import kotlin.collections.HashMap

open class OfflineCloudPlayer(
        private val name: String,
        private val uniqueId: UUID,
        private val firstLogin: Long,
        private val lastLogin: Long,
        private val onlineTime: Long
) : IOfflineCloudPlayer {
    val properties = HashMap<String, String>()

    override fun getName(): String = this.name

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getLastLogin(): Long = this.lastLogin

    override fun getFirstLogin(): Long = this.firstLogin

    override fun getOnlineTime(): Long = this.onlineTime

    override fun getProperty(name: String): String? = properties[name]

    override fun setProperty(name: String, content: String) {
        properties[name] = content
    }


}