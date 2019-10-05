package eu.thesimplecloud.lib.servicegroup.impl

import eu.thesimplecloud.lib.servicegroup.grouptype.ICloudLobbyGroup

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
        private var permission: String,
        moduleNamesToCopy: List<String> = emptyList(),
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
        moduleNamesToCopy,
        hiddenAtProxyGroups
), ICloudLobbyGroup {

    override fun getPriority(): Int = this.priority

    override fun getPermission(): String = this.permission

    override fun setPriority(priority: Int) {
        this.priority = priority
    }

    override fun setPermission(permission: String) {
        this.permission = permission
    }
}