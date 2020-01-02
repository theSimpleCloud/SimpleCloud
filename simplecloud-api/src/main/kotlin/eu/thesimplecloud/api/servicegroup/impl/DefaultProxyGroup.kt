package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudProxyGroup

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
        serviceVersion: ServiceVersion,
        startPriority: Int
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
        serviceVersion,
        startPriority
), ICloudProxyGroup {

    override fun getStartPort(): Int = this.startPort

    override fun setStartPort(port: Int) {
        this.startPort = port
    }

    override fun toString(): String {
        return JsonData.fromObjectWithGsonExclude(this).getAsJsonString()
    }

}