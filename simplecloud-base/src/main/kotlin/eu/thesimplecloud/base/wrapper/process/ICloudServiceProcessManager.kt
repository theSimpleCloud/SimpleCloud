package eu.thesimplecloud.base.wrapper.process

interface ICloudServiceProcessManager {

    /**
     * Registers a service process on this wrapper.
     */
    fun registerServiceProcess(cloudServiceProcess: ICloudServiceProcess)

    /**
     * Registers a service process on this wrapper.
     */
    fun unregisterServiceProcess(cloudServiceProcess: ICloudServiceProcess)

    /**
     * Returns a list of all registered cloud processes
     */
    fun getAllProcesses(): Set<ICloudServiceProcess>

    /**
     * Returns the [ICloudServiceProcess] found by the specified service [name]
     */
    fun getCloudServiceProcessByServiceName(name: String): ICloudServiceProcess? = getAllProcesses().firstOrNull { it.getCloudService().getName().equals(name, true) }

    /**
     * Stops all registered services.
     */
    fun stopAllServices() = getAllProcesses().forEach { it.shutdown() }

}