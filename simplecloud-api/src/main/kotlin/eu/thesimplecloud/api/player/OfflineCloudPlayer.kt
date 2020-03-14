package eu.thesimplecloud.api.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.property.Property
import java.util.*
import kotlin.collections.HashMap

open class OfflineCloudPlayer(
        private val name: String,
        private val uniqueId: UUID,
        private val firstLogin: Long,
        private val lastLogin: Long,
        private val onlineTime: Long,
        var propertyMap: MutableMap<String, Property<*>> = HashMap()
) : IOfflineCloudPlayer {


    override fun getName(): String = this.name

    @JsonIgnore
    override fun getProperties(): Map<String, Property<*>> {
        return this.propertyMap
    }
    override fun getUniqueId(): UUID = this.uniqueId

    override fun getLastLogin(): Long = this.lastLogin

    override fun getFirstLogin(): Long = this.firstLogin

    override fun getOnlineTime(): Long = this.onlineTime

    override fun <T : Any> setProperty(name: String, property: Property<T>) {
        this.propertyMap[name] = property
    }


}