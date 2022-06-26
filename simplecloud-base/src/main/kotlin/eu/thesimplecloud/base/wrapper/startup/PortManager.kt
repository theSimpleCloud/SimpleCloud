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

package eu.thesimplecloud.base.wrapper.startup

import java.io.IOException
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap


class PortManager {

    companion object {
        const val START_PORT = 50_000
    }

    private val usedPorts = ConcurrentHashMap.newKeySet<Int>()

    private val blockedPorts = ConcurrentHashMap.newKeySet<Int>()


    @Synchronized
    fun getUnusedPort(startPort: Int = START_PORT): Int {
        var port = startPort
        while (isInUse(port) || isPortBlockedByOtherApp(port)) {
            port++
        }
        usedPorts.add(port)

        return port
    }

    private fun isPortBlockedByOtherApp(port: Int): Boolean {
        if (this.blockedPorts.contains(port)) {
            return true
        }
        val isPortAvailable = isPortAvailable(port)
        if (!isPortAvailable) {
            addBlockedPort(port)
        }
        return !isPortAvailable
    }

    private fun isInUse(port: Int): Boolean {
        return this.usedPorts.contains(port)
    }

    private fun isPortAvailable(port: Int): Boolean {
        try {
            Socket("127.0.0.1", port).use { return false }
        } catch (ignored: IOException) {
            return true
        }
    }

    fun setPortUnused(port: Int) {
        this.usedPorts.remove(port)
    }

    private fun addBlockedPort(port: Int) {
        if (this.blockedPorts.size > 200) {
            this.blockedPorts.clear()
        }
        this.blockedPorts.add(port)
    }

}