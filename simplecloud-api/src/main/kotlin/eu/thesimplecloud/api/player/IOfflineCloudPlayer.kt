package eu.thesimplecloud.api.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.property.IPropertyMap
import eu.thesimplecloud.api.utils.Nameable
import java.util.*

interface IOfflineCloudPlayer : Nameable, IPropertyMap {

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
     * Returns whether this player is connected to the network..
     */
    @JsonIgnore
    fun isOnline(): Boolean = false

}