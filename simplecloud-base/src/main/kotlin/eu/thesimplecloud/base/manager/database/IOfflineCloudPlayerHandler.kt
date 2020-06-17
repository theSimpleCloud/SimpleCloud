/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
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

package eu.thesimplecloud.base.manager.database

import eu.thesimplecloud.api.player.IOfflineCloudPlayer
import eu.thesimplecloud.api.player.OfflineCloudPlayer
import eu.thesimplecloud.api.property.Property
import eu.thesimplecloud.base.manager.player.LoadOfflineCloudPlayer
import eu.thesimplecloud.base.manager.player.exception.OfflinePlayerLoadException
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.jsonlib.JsonLib
import java.util.*

interface IOfflineCloudPlayerHandler {

    /**
     * Returns the [IOfflineCloudPlayer] found by the specified [playerUniqueId]
     */
    fun getOfflinePlayer(playerUniqueId: UUID): IOfflineCloudPlayer?

    /**
     * Returns the [IOfflineCloudPlayer] found by the specified [name]
     */
    fun getOfflinePlayer(name: String): IOfflineCloudPlayer?

    /**
     * Saves the specified [offlineCloudPlayer] to the database.
     */
    fun saveCloudPlayer(offlineCloudPlayer: OfflineCloudPlayer)

    /**
     * Closes the connection to the database
     */
    fun closeConnection()


    fun fromLoadOfflinePlayer(loadOfflineCloudPlayer: LoadOfflineCloudPlayer?): OfflineCloudPlayer? {
        loadOfflineCloudPlayer ?: return null
        val propertyMapAsDocument = loadOfflineCloudPlayer.propertyMap
        try {
            val propertyMap = propertyMapAsDocument.toSortedMap().mapValues {
                val valueString = it.value.toString()
                val jsonLib = JsonLib.fromJsonString(valueString)
                val className = jsonLib.getString("className")!!
                val clazz = this.findClass(className)
                val value = jsonLib.getObject("savedValue", clazz)!!
                Property(value)
            }
            return OfflineCloudPlayer(
                    loadOfflineCloudPlayer.name,
                    loadOfflineCloudPlayer.uniqueId,
                    loadOfflineCloudPlayer.firstLogin,
                    loadOfflineCloudPlayer.lastLogin,
                    loadOfflineCloudPlayer.onlineTime,
                    loadOfflineCloudPlayer.lastPlayerConnection,
                    HashMap(propertyMap)
            )
        } catch (ex: Exception) {
            throw OfflinePlayerLoadException("Error while loading OfflinePlayer ${loadOfflineCloudPlayer.name}:", ex)
        }
    }

    fun findClass(className: String): Class<*> {
        return Manager.instance.cloudModuleHandler.findModuleOrSystemClass(className)
    }

}