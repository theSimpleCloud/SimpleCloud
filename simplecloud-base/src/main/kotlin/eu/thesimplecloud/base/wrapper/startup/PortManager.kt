package eu.thesimplecloud.base.wrapper.startup

import java.net.ServerSocket

class PortManager {

    val START_PORT = 50_000

    private val usedPorts = HashSet<Int>()


    @Synchronized
    fun getUnusedPort(startPort: Int = START_PORT): Int {
        var port = startPort
        while (usedPorts.contains(port)) {
            port++
        }
        usedPorts.add(port)

        /*val serverSocket = ServerSocket(0)
        serverSocket.localPort
        serverSocket.close()*/

        return port
    }

    fun setPortUnused(port: Int) {
        this.usedPorts.remove(port)
    }


}