/*
 * MIT License
 *
 * Copyright (C) 2020-2022 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.player

import com.google.common.collect.Maps
import eu.thesimplecloud.api.player.connection.DefaultPlayerConnection
import eu.thesimplecloud.api.player.connection.IPlayerConnection
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.property.Property
import java.util.*
import java.util.concurrent.ConcurrentMap

open class OfflineCloudPlayer(
    name: String,
    uniqueId: UUID,
    private val firstLogin: Long,
    private val lastLogin: Long,
    private val onlineTime: Long,
    protected val lastPlayerConnection: DefaultPlayerConnection,
    @Volatile var propertyMap: ConcurrentMap<String, Property<*>> = Maps.newConcurrentMap()
) : SimpleCloudPlayer(name, uniqueId), IOfflineCloudPlayer {

    @Volatile
    private var displayName = name

    override fun getProperties(): Map<String, IProperty<*>> {
        return this.propertyMap
    }

    override fun getLastLogin(): Long = this.lastLogin

    override fun getFirstLogin(): Long = this.firstLogin

    override fun getOnlineTime(): Long = this.onlineTime

    override fun getLastPlayerConnection(): IPlayerConnection = this.lastPlayerConnection

    override fun setDisplayName(displayName: String) {
        this.displayName = displayName
    }

    override fun getDisplayName(): String {
        return this.displayName
    }

    override fun toOfflinePlayer(): IOfflineCloudPlayer {
        return OfflineCloudPlayer(
            getName(),
            getUniqueId(),
            getFirstLogin(),
            getLastLogin(),
            getOnlineTime(),
            this.lastPlayerConnection,
            this.propertyMap
        )
    }

    override fun <T : Any> setProperty(name: String, value: T): IProperty<T> {
        require(value !is Property<*>) { "Cannot set ${value::class.java.name} as property" }
        val property = Property(value)
        this.propertyMap[name] = property
        return property
    }

    override fun clearProperties() {
        this.propertyMap.clear()
    }

    override fun removeProperty(name: String) {
        this.propertyMap.remove(name)
    }


}