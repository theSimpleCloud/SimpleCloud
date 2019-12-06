package eu.thesimplecloud.lib.player

import eu.thesimplecloud.lib.utils.Nameable
import java.util.*

interface IOfflineCloudPlayer : Nameable {

    /**
     * Returns the unique id of this player.
     */
    fun getUniqueId(): UUID

    /**
     * Returns the timestamp of the last login.
     */
    fun getLastLogin(): Long


    /**
     * Returns the timestamp of the first login.
     */
    fun getFirstLogin(): Long

    /**
     * Returns the online time of this player in milliseconds
     */
    fun getOnlineTime(): Long

    /**
     * Returns a property.
     */
    fun getProperty(name: String): String?

    /**
     * Sets a property.
     */
    fun setProperty(name: String, content: String)

}