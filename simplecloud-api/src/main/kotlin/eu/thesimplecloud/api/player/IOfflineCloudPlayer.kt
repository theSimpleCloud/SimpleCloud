package eu.thesimplecloud.api.player

import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.utils.Nameable
import java.util.*

interface IOfflineCloudPlayer : Nameable, IProperty {

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

}