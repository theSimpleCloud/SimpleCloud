package eu.thesimplecloud.api.player

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.connection.IPlayerConnection
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import java.util.*
import kotlin.collections.HashMap

open class OfflineCloudPlayer(
        name: String,
        uniqueId: UUID,
        private val firstLogin: Long,
        private val lastLogin: Long,
        private val onlineTime: Long,
        protected val playerConnection: DefaultPlayerConnection,
        var propertyMap: MutableMap<String, Property<*>> = HashMap()
): SimpleCloudPlayer(name, uniqueId), IOfflineCloudPlayer {

    @JsonIgnore
    override fun getProperties(): Map<String, IProperty<*>> {
        return this.propertyMap
    }

    override fun getLastLogin(): Long = this.lastLogin

    override fun getFirstLogin(): Long = this.firstLogin

    override fun getOnlineTime(): Long = this.onlineTime

    override fun getLastPlayerConnection(): IPlayerConnection = this.playerConnection

    override fun <T : Any> setProperty(name: String, value: T): IProperty<T> {
        require(value !is Property<*>) { "Cannot set ${value::class.java.name} as property" }
        val property = Property(value)
        this.propertyMap[name] = property
        return property
    }

    override fun removeProperty(name: String) {
        this.propertyMap.remove(name)
    }


}