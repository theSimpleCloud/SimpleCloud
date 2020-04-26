package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap

interface ICloudServiceProcess : IBootstrap {

    /**
     * Returns the [ICloudService] this process executes
     */
    fun getCloudService(): ICloudService

    /**
     * Terminates this process immediately (not recommended to use)
     */
    fun forceStop()

    /**
     * Executes a command on this service
     */
    fun executeCommand(command: String)



}