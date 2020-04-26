package eu.thesimplecloud.api.servicegroup.impl

import eu.thesimplecloud.api.service.ServiceVersion
import eu.thesimplecloud.api.servicegroup.grouptype.ICloudLobbyGroup
import eu.thesimplecloud.clientserverapi.lib.json.JsonData

class DefaultLobbyGroup(
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
        private var priority: Int,
        private var permission: String?,
        serviceVersion: ServiceVersion,
        startPriority: Int,
        hiddenAtProxyGroups: List<String> = emptyList()
) : DefaultServerGroup(
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
        startPriority,
        hiddenAtProxyGroups
), ICloudLobbyGroup {

    override fun getPriority(): Int = this.priority

    override fun getPermission(): String? = this.permission

    override fun setPriority(priority: Int) {
        this.priority = priority
    }

    override fun setPermission(permission: String) {
        this.permission = permission
    }

    override fun toString(): String {
        return JsonData.fromObjectWithGsonExclude(this).getAsJsonString()
    }
}