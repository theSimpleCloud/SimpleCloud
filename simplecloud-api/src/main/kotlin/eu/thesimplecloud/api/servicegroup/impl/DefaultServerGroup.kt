package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudServerGroup

open class DefaultServerGroup(
        name: String,
        templateName: String,
        maxMemory: Int,
        maxPlayers: Int,
        minimumOnlineServiceCount: Int,
        maximumOnlineServiceCount: Int,
        maintenance: Boolean,
        static: Boolean,
        percentToStartNewService: Int,
        wrapperName: String?,
        serviceVersion: ServiceVersion,
        startPriority: Int,
        private val hiddenAtProxyGroups: List<String> = emptyList()
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
), ICloudServerGroup {

    override fun getHiddenAtProxyGroups(): List<String> = this.hiddenAtProxyGroups

    override fun toString(): String {
        return JsonData.fromObjectWithGsonExclude(this).getAsJsonString()
    }
}