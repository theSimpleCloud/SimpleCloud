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

package eu.thesimplecloud.base.manager.packet

import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import org.reflections.Reflections

interface IPacketRegistry {

    /**
     * Registers a packet
     */
    fun registerPacket(cloudModule: ICloudModule, packetClass: Class<out IPacket>)

    /**
     * Registers all Packets in the specified [packages]
     */
    fun registerPacketsByPackage(cloudModule: ICloudModule, vararg packages: String) {
        for (packageName in packages) {
            val reflections = Reflections(packageName)
            val allClasses = reflections.getSubTypesOf(IPacket::class.java)
                .union(reflections.getSubTypesOf(JsonPacket::class.java))
                .union(reflections.getSubTypesOf(ObjectPacket::class.java))
                .union(reflections.getSubTypesOf(BytePacket::class.java))
                .filter { it != JsonPacket::class.java && it != BytePacket::class.java && it != ObjectPacket::class.java }

            allClasses.forEach { packetClass -> registerPacket(cloudModule, packetClass) }
        }
    }

    /**
     * Unregisters a packet
     */
    fun unregisterAllPackets(cloudModule: ICloudModule)

}