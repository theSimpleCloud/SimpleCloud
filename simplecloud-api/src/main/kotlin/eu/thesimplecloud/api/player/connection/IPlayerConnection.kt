/*
 * MIT License
 *
 * Copyright (C) 2020 SimpleCloud-Team
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

package eu.thesimplecloud.api.player.connection

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.thesimplecloud.api.CloudAPI
import java.util.*

interface IPlayerConnection {

    /**
     * Returns the address of this player.
     */
    fun getAddress(): IPlayerAddress

    /**
     * Returns the unique id of this player
     */
    fun getUniqueId(): UUID

    /**
     * Returns the username of this player
     */
    fun getName(): String

    /**
     * Returns whether the player's connection is in online mode.
     */
    fun isOnlineMode(): Boolean

    /**
     * Get the numerical client version of the player attempting to log in.
     *
     * @return the protocol version of the client
     */
    fun getVersion(): Int

    /**
     * Returns the cloud player this player connection is linked to
     */
    @JsonIgnore
    fun getCloudPlayer() = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(getUniqueId())

}