package eu.thesimplecloud.module.placeholders.spigot

import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.module.permission.PermissionPool
import eu.thesimplecloud.module.prefix.service.tablist.ProxyTablistHelper
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.Bukkit
import java.util.UUID

fun String.replace(
    uuid: UUID,
    replacePermissionModule: Boolean = false,
    replaceChatTabModule: Boolean = false
): String {

    var replacedString = this

    if (replacePermissionModule) {
        replacedString = replacedString.replacePermissionModule(uuid)
    }

    if (replaceChatTabModule) {
        replacedString = replacedString.replaceChatTabModule(uuid)
    }

    val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCloudPlayer(uuid).getBlockingOrNull()
    val proxy = cloudPlayer?.getConnectedProxy()
    val thisService = CloudPlugin.instance.thisService()
    val wrapper = proxy?.getWrapper()
    val cloudPlayerManager = CloudAPI.instance.getCloudPlayerManager()

    return replacedString
        .replace("%ONLINE_PLAYERS%", cloudPlayerManager.getNetworkOnlinePlayerCount().get().toString())
        .replace("%MAX_PLAYERS%", proxy?.getMaxPlayers().toString())
        .replace("%REGISTERED_PLAYER_COUNT%", cloudPlayerManager.getRegisteredPlayerCount().get().toString())

        .replace("%WRAPPER%", wrapper?.getName().toString())
        .replace("%WRAPPER_HOST%", wrapper?.getHost().toString())
        .replace("%WRAPPER_MAX_MEMORY%", wrapper?.getMaxMemory().toString())
        .replace("%WRAPPER_USED_MEMORY%", wrapper?.getUsedMemory().toString())
        .replace("%WRAPPER_CPU_USAGE%", wrapper?.getCpuUsage().toString())
        .replace("%WRAPPER_CURRENTLY_STARTING_SERVICES%", wrapper?.getCurrentlyStartingServices().toString())
        .replace("%WRAPPER_SERVICE_COUNT%", wrapper?.getServicesRunningOnThisWrapper()?.size.toString())

        .replace("%PROXY%", proxy?.getName().toString())
        .replace("%PROXY_HOST%", proxy?.getHost().toString())
        .replace("%PROXY_STATE%", proxy?.getState().toString())
        .replace("%PROXY_NUMBER%", proxy?.getServiceNumber().toString())
        .replace("%PROXY_PORT%", proxy?.getPort().toString())
        .replace("%PROXY_DISPLAYNAME%", proxy?.getDisplayName().toString())
        .replace("%PROXY_GROUP%", proxy?.getGroupName().toString())
        .replace("%PROXY_ONLINE_PLAYERS%", proxy?.getOnlinePlayers()?.get()?.size.toString())
        .replace("%PROXY_MAX_PLAYERS%", proxy?.getMaxPlayers().toString())
        .replace("%PROXY_MAX_MEMORY%", proxy?.getMaxMemory().toString())
        .replace("%PROXY_USED_MEMORY%", proxy?.getUsedMemory().toString())
        .replace("%PROXY_TEMPLATE%", proxy?.getTemplateName().toString())

        .replace("%SERVER%", thisService.getName())
        .replace("%SERVER_HOST%", thisService.getHost())
        .replace("%SERVER_STATE%", thisService.getState().toString())
        .replace("%SERVER_NUMBER%", thisService.getServiceNumber().toString())
        .replace("%SERVER_PORT%", thisService.getPort().toString())
        .replace("%SERVER_DISPLAYNAME%", thisService.getDisplayName())
        .replace("%SERVER_GROUP%", thisService.getGroupName())
        .replace("%SERVER_ONLINE_PLAYERS%", Bukkit.getOnlinePlayers().size.toString())
        .replace("%SERVER_MAX_PLAYERS%", Bukkit.getMaxPlayers().toString())
        .replace("%SERVER_MAX_MEMORY%", thisService.getMaxMemory().toString())
        .replace("%SERVER_USED_MEMORY%", thisService.getUsedMemory().toString())
        .replace("%SERVER_TEMPLATE%", thisService.getTemplateName())

        .replace("%PLAYER_NAME%", cloudPlayer?.getName().toString())
        .replace("%PLAYER_PING%", Bukkit.getPlayer(uuid)?.ping.toString())
}

fun String.replacePermissionModule(uuid: UUID): String {

    var replacedString = this

    try {
        //Using cached player because the player is definitely online when this method is called and should be cached.
        val permissionPlayer = PermissionPool.instance.getPermissionPlayerManager().getCachedPermissionPlayer(uuid)

        replacedString = replacedString
            .replace("%RANK_NAME%", permissionPlayer?.getHighestPermissionGroup()?.getName().toString())
            .replace("%RANK_PRIORITY%", permissionPlayer?.getHighestPermissionGroup()?.getPriority().toString())
    } catch (_: Exception) {
    }

    return replacedString
}

fun String.replaceChatTabModule(uuid: UUID): String {

    var replacedString = this

    try {
        val tablistInformation = ProxyTablistHelper.getTablistInformationByUUID(uuid)

        replacedString = replacedString
            .replace("%RANK_COLOR%", tablistInformation?.color.toString())
            .replace("%RANK_PREFIX%", tablistInformation?.prefix.toString())
            .replace("%RANK_SUFFIX%", tablistInformation?.suffix.toString())
            .replace("%RANK_TAB_PRIORITY%", tablistInformation?.priority.toString())
    } catch (_: Exception) {
    }

    return replacedString
}