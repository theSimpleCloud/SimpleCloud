package eu.thesimplecloud.module.permission.service.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.permission.PermissionsSetupEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.permission.group.manager.PermissionGroupManager

@Plugin(id = "simplecloud_permission", dependencies = [Dependency(id = "simplecloud_plugin")])
class VelocityPluginMain @Inject constructor(val proxyServer: ProxyServer) {

    private val permissionProvider = VelocityPermissionProvider()

    @Subscribe
    fun on(event: ProxyInitializeEvent) {
        PermissionPool(PermissionGroupManager())
    }

    @Subscribe
    fun on(event: PermissionsSetupEvent) {
        PermissionPool(PermissionGroupManager())
        event.provider = permissionProvider
    }

}