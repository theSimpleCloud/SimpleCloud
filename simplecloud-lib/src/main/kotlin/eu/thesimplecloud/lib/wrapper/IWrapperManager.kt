package eu.thesimplecloud.lib.wrapper
import eu.thesimplecloud.lib.service.ICloudService

interface IWrapperManager {

    /**
     * Updates or adds a [IWrapperInfo]
     */
    fun updateWrapper(wrapper: IWrapperInfo)

    /**
     * Removes the specified [IWrapperInfo]
     */
    fun removeWrapper(wrapper: IWrapperInfo)

    /**
     * Returns all registered wrappers
     */
    fun getAllWrappers(): List<IWrapperInfo>

    /**
     * Returns the [IWrapperInfo] found by the specified name
     */
    fun getWrapperByName(name: String): IWrapperInfo? = getAllWrappers().firstOrNull { it.getName().equals(name, true) }

    /**
     * Returns the [IWrapperInfo] found by the specified host
     */
    fun getWrapperByHost(host: String): IWrapperInfo? = getAllWrappers().firstOrNull { it.getHost().equals(host, true) }



}