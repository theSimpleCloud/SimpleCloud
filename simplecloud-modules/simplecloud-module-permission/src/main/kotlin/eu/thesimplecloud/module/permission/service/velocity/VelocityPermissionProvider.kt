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

package eu.thesimplecloud.module.permission.service.velocity

import com.velocitypowered.api.permission.PermissionFunction
import com.velocitypowered.api.permission.PermissionProvider
import com.velocitypowered.api.permission.PermissionSubject
import com.velocitypowered.api.permission.Tristate
import com.velocitypowered.api.proxy.Player
import eu.thesimplecloud.module.permission.PermissionPool

class VelocityPermissionProvider : PermissionProvider {

    override fun createFunction(subject: PermissionSubject): PermissionFunction? {
        if (subject !is Player) return PermissionFunction.ALWAYS_TRUE
        return CloudPermissionFunction(subject)
    }

    private class CloudPermissionFunction(private val player: Player) : PermissionFunction {


        override fun getPermissionValue(permission: String?): Tristate {
            if (permission == null) return Tristate.FALSE
            val permissionPlayer = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(player.uniqueId)
            if (permissionPlayer == null) {
                println("WARNING: PermissionPlayer is NULL (${player.username})")
                return Tristate.FALSE
            }

            return Tristate.fromBoolean(permissionPlayer.hasPermission(permission))
        }

    }

}