package eu.thesimplecloud.base.wrapper.process.serviceconfigurator

import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators.DefaultProxyConfigurator
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators.DefaultServerConfigurator
import eu.thesimplecloud.lib.service.ServiceVersion

class ServiceConfiguratorManager {

    private val configurationMap = mapOf(ServiceVersion.ServiceVersionType.PROXY_DEFAULT to DefaultProxyConfigurator(),
            ServiceVersion.ServiceVersionType.SERVER_DEFAULT to DefaultServerConfigurator())

    /**
     * Returns the [IServiceConfigurator] found by the specified [ServiceVersion.ServiceVersionType]
     */
    fun getServiceConfigurator(serviceVersionType: ServiceVersion.ServiceVersionType): IServiceConfigurator? = configurationMap[serviceVersionType]


}