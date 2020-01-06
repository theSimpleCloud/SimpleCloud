package eu.thesimplecloud.base.wrapper.startup

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
        return port
    }

    fun setPortUnused(port: Int) {
        this.usedPorts.remove(port)
    }


}