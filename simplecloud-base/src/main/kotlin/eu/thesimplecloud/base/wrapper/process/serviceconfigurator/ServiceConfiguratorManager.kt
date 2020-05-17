package eu.thesimplecloud.base.wrapper.process.serviceconfigurator

import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators.DefaultBungeeConfigurator
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators.DefaultServerConfigurator
import eu.thesimplecloud.base.wrapper.process.serviceconfigurator.configurators.DefaultVelocityConfigurator

class ServiceConfiguratorManager {

    private val configurationMap = mapOf(
            ServiceVersion.ServiceVersionType.VELOCITY_DEFAULT to DefaultVelocityConfigurator(),
            ServiceVersion.ServiceVersionType.BUNGEE_DEFAULT to DefaultBungeeConfigurator(),
            ServiceVersion.ServiceVersionType.SERVER_DEFAULT to DefaultServerConfigurator())

    /**
     * Returns the [IServiceConfigurator] found by the specified [ServiceVersion.ServiceVersionType]
     */
    fun getServiceConfigurator(serviceVersionType: ServiceVersion.ServiceVersionType): IServiceConfigurator? = configurationMap[serviceVersionType]


}