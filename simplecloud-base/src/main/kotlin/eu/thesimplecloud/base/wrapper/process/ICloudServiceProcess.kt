package eu.thesimplecloud.base.wrapper.process

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.lib.service.ICloudService

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