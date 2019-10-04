package eu.thesimplecloud.lib.bootstrap

import eu.thesimplecloud.clientserverapi.lib.bootstrap.IBootstrap
import eu.thesimplecloud.lib.service.ICloudServiceManager
import eu.thesimplecloud.lib.servicegroup.ICloudServiceGroupManager
import eu.thesimplecloud.lib.wrapper.IWrapperManager

/**
 * It represents the main part of a cloud part
 * The Wrapper, Manager and CloudPlugin should implement this interface.
 */
interface ICloudBootstrap : IBootstrap {

    /**
     * Returns the [IWrapperManager] to manage wrapper
     */
    fun getWrapperManager(): IWrapperManager

    /**
     * Returns the [ICloudServiceGroupManager] to manage service groups
     */
    fun getCloudServiceGroupManager(): ICloudServiceGroupManager

    /**
     * Returns the [ICloudServiceManager] to manage services
     */
    fun getCloudServiceManger(): ICloudServiceManager

}