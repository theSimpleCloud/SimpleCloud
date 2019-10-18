package eu.thesimplecloud.lib.servicegroup.impl

import eu.thesimplecloud.lib.service.ServiceVersion
import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudProxyGroup

class DefaultProxyGroup(
        name: String,
        templateName: String,
        maxMemory: Int,
        maxPlayers: Int,
        minimumOnlineServiceCount: Int,
        maximumOnlineServiceCount: Int,
        maintenance: Boolean,
        static: Boolean,
        percentToStartNewService: Int,
        wrapperName: String,
        private var startPort: Int,
        serviceVersion: ServiceVersion
) : AbstractServiceGroup(
        name,
        templateName,
        maxMemory,
        maxPlayers,
        minimumOnlineServiceCount,
        maximumOnlineServiceCount,
        maintenance,
        static,
        percentToStartNewService,
        wrapperName,
        serviceVersion
), ICloudProxyGroup {

    override fun getStartPort(): Int = this.startPort

    override fun setStartPort(port: Int) {
        this.startPort = port
    }

}