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

package eu.thesimplecloud.module.permission.player.manager

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.module.permission.player.IPermissionPlayer
import eu.thesimplecloud.module.permission.player.PermissionPlayer
import java.util.*

interface IPermissionPlayerManager {


    /**
     * Returns a list of all cached permission players
     */
    fun getAllCachedPermissionPlayers(): List<IPermissionPlayer> =
        CloudAPI.instance.getCloudPlayerManager().getAllCachedObjects()
            .mapNotNull { it.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue() }

    /**
     * Returns the first [IPermissionPlayer] found by the specified [name]
     */
    fun getCachedPermissionPlayer(name: String): IPermissionPlayer? =
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(name)
            ?.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue()

    /**
     * Returns the first [IPermissionPlayer] found by the specified [uniqueId]
     */
    fun getCachedPermissionPlayer(uniqueId: UUID): IPermissionPlayer? =
        CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(uniqueId)
            ?.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue()

    /**
     * Sends a packet to the manager to get the requested [IPermissionPlayer] and returns its result
     */
    fun getPermissionPlayer(name: String): ICommunicationPromise<IPermissionPlayer> =
        CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(name)
            .then { it.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue() }

    /**
     * Sends a packet to the manager to get the requested [IPermissionPlayer] and returns its result
     */
    fun getPermissionPlayer(uniqueId: UUID): ICommunicationPromise<IPermissionPlayer> =
        CloudAPI.instance.getCloudPlayerManager().getOfflineCloudPlayer(uniqueId)
            .then { it.getProperty<PermissionPlayer>(PermissionPlayer.PROPERTY_NAME)?.getValue() }


}