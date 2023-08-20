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

package eu.thesimplecloud.api.service

import eu.thesimplecloud.api.network.component.IAuthenticatable
import eu.thesimplecloud.api.utils.time.Timestamp

/**
 * Created by IntelliJ IDEA.
 * Date: 18.01.2021
 * Time: 17:13
 * @author Frederick Baier
 */
interface ICloudServiceVariables : IAuthenticatable {

    /**
     * Returns the state of this service.
     */
    fun getState(): ServiceState

    /**
     * Returns the amount of players that are currently on this service
     */
    fun getOnlineCount(): Int

    /**
     * Returns the last time stamp a player interacted with the server.
     */
    fun getLastPlayerUpdate(): Timestamp

    /**
     * Returns the maximum amount of players for this service
     */
    fun getMaxPlayers(): Int

    /**
     * Returns the MOTD of this service.
     */
    fun getMOTD(): String

    /**
     * Returns the display name of this service.
     */
    fun getDisplayName(): String

    /**
     * Returns weather this service is joinable for players.
     */
    fun isServiceJoinable() = getState() == ServiceState.VISIBLE || getState() == ServiceState.INVISIBLE

    /**
     * Sets the state of this service
     */
    fun setState(serviceState: ServiceState)

    /**
     * Sets the amount of online players.
     */
    fun setOnlineCount(amount: Int)

    /**
     * Sets the last time a player interacted with the server.
     */
    fun setLastPlayerUpdate(timeStamp: Timestamp)

    /**
     * Sets the maximum amount of players for this service.
     * The amount has no effect on players trying to join.
     */
    fun setMaxPlayers(amount: Int)

    /**
     * Sets the MOTD of this service.
     */
    fun setMOTD(motd: String)

    /**
     * Set the display name of this service.
     */
    fun setDisplayName(displayname: String)
}